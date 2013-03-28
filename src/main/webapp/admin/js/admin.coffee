root = exports ? this

# compile ALL THE templates!
templates = {}
$('script[type="text/x-handlebars-template"]').each ->
  $el = $(this)
  id = $el.attr 'id'
  templates[id] = Handlebars.compile $el.html()
  $el.remove()

BASE_URL = window.BASE_URL

class Page
  constructor: ->
    @el = $('#' + @id)

  activate: ->
    $('.wrapper .page.current').removeClass('current')
    @el.addClass('current')

  render: ->

  show: ->
    @render()
    @bindEvents() if @bindEvents
    @activate()
    @ # allow chaining

class OverviewPage extends Page
  id: 'survey-overview'

  render: ->
    $.ajax {
      url: BASE_URL + '/api/admin/survey'
      dataType: 'json'
      success: @fillSurveyList
    }

  bindEvents: ->
    $('ul, #create-survey-button').off 'click'
    $('ul', @el).on 'click', 'button', @buttonClicked
    $('#create-survey-button', @el).on 'click', ->
      Admin.pages.newSurvey.show()

  buttonClicked: (ev) =>
    ev.preventDefault()
    btn = $(ev.target)
    return false unless btn.is 'button'
    surveyId = btn.parent().parent().data('surveyId')
    return false if typeof surveyId is 'undefined'
    name = btn.attr('name')
    if name == 'delete'
      return false unless window.confirm('Do you really want to delete Survey #' + surveyId + '?' + "\n\n" + 'ALL DATA WILL BE LOST!')
      @deleteSurvey surveyId
    else if name == 'edit'
      Admin.pages.editSurvey.setSurveyId(surveyId).show()

  fillSurveyList: (data) ->
    html = '';
    for survey in data
      html += templates['survey-list-template'](survey)
    $('ul', @el).empty().append(html)

  deleteSurvey: (id) ->
    $.ajax {
      type: 'delete'
      url: BASE_URL + '/api/admin/survey/' + id
      dataType: 'json'
      success: (data) =>
        if data.id > 0
          # success
          alert 'Survey #' + id + ' has been successfully deleted.'
          Admin.pages.overview.show()
      error: ->
        alert 'Your Survey could not be deleted. Please try again / check the console.'
    }

  highlightSurvey: (id) ->
    surveyEl = $('li[data-survey-id="' + id + '"]', @el)
    return false unless surveyEl.length > 0
    originalBgColor = surveyEl.css("backgroundColor") || 'transparent'
    surveyEl.css('backgroundColor', '#ffff99').animate({
      backgroundColor: originalBgColor
    }, 1300)

  # TODO: Add survey preview box

class NewPage extends Page
  id: 'survey-new'
  jsonTemplate: null

  render: ->
    $.ajax {
      url: BASE_URL + '/api/admin/survey/0'
      dataType: 'text' # it is json, but we need it in text-form
      headers:
        'Accept': 'application/json, text/plain'
      success: (data) =>
        @jsonTemplate = data
        @fillSurveyTemplate()
    }

  bindEvents: ->
    $('#insert-template-button, #submit-new-survey-button').off 'click' # remove all previous event handlers
    $('#insert-template-button', @el).on 'click', (ev) =>
      ev.preventDefault()
      @fillSurveyTemplate()
      false
    $('#submit-new-survey-button', @el).on 'click', (ev) =>
      ev.preventDefault()
      @saveSurvey()
      false

  fillSurveyTemplate: ->
    return false if @jsonTemplate == null
    data = @jsonTemplate
    data = formatter.formatJson data if formatter?.formatJson # make it nice, when formatJson is available
    $('textarea', @el).val data
    true

  saveSurvey: ->
    surveyJson = $('textarea', @el).val()
    $.ajax {
      type: 'put'
      url: BASE_URL + '/api/admin/survey'
      data: surveyJson
      processData: false
      contentType: 'application/json'
      dataType: 'json'
      success: (data) =>
        if data.id > 0
          # success
          alert 'Survey #' + data.id + ' has been successfully created.'
          Admin.pages.overview.show().highlightSurvey(data.id)
      error: ->
        alert 'Your Survey could not be saved. Please try again / check the console.'
    }

class EditPage extends Page
  id: 'survey-edit'
  surveyId: null

  setSurveyId: (id) ->
    @surveyId = id
    @ # allow chaining

  render: ->
    return false if @surveyId == null
    @el.empty().append '<h3>Edit Survey ' + @surveyId + '</h3><em>Loading Survey data...</em>'
    $.ajax {
      url: BASE_URL + '/api/admin/survey/' + @surveyId
      dataType: 'text'
      headers:
        'Accept': 'application/json, text/plain'
      success: @renderWithSurveyJSON
      error: =>
        alert 'Survey #' + @surveyId + ' could not be loaded. Please try again / check the console.'
        Admin.pages.overview.show().highlightSurvey(@surveyId)
    }

  renderWithSurveyJSON: (jsontext) =>
    survey = {id: @surveyId, json: jsontext}
    survey.json = formatter.formatJson survey.json if formatter?.formatJson
    @el.empty().append templates['survey-edit-template'](survey)
    @bindEvents()

  bindEvents: ->
    $('#submit-edit-survey-button').off 'click' # remove all previous event handlers
    $('#submit-edit-survey-button', @el).on 'click', (ev) =>
      ev.preventDefault()
      @saveSurvey()
      false

  saveSurvey: =>
    surveyId = parseInt $('form', @el).data('surveyId') || '-1'
    return false unless surveyId > 0
    surveyJson = $('textarea', @el).val()
    $.ajax {
      type: 'post'
      url: BASE_URL + '/api/admin/survey/' + surveyId
      data: surveyJson
      processData: false
      contentType: 'application/json'
      dataType: 'json'
      success: (data) =>
        if data.id > 0
          # success
          alert 'Survey #' + data.id + ' has been successfully edited.'
          Admin.pages.overview.show().highlightSurvey(data.id)
      error: ->
        alert 'Your Survey could not be saved. Please try again / check the console.'
    }

Admin = root.Admin =
  pages: {
    overview: new OverviewPage
    newSurvey: new NewPage
    editSurvey: new EditPage
  }

  run: ->
    Admin.pages.overview.show()

    $('header h1').addClass('clickable').on 'click', ->
      Admin.pages.overview.show()

Admin.run()