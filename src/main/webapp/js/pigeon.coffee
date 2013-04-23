# Copyright (C) 2013 headissue GmbH (www.headissue.com)
#
# Source repository: https://github.com/headissue/pigeon
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This patch is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this patch.  If not, see <http://www.gnu.org/licenses/agpl.txt/>.

root = exports ? this

# embedding Gator, remove it from the window object afterwards.
# TODO: maybe modify Gators code to never touch window.Gator?
_prevGator = null
if typeof window.Gator != "undefined"
  _prevGator = window.Gator

`
/* gator v1.1.1 craig.is/riding/gators */
(function(){function r(a,c,b){if("_root"==c)return b;if(a!==b){var j;e||(a.matches&&(e=a.matches),a.webkitMatchesSelector&&(e=a.webkitMatchesSelector),a.mozMatchesSelector&&(e=a.mozMatchesSelector),a.msMatchesSelector&&(e=a.msMatchesSelector),a.oMatchesSelector&&(e=a.oMatchesSelector),e||(e=g.matchesSelector));j=e;if(j.call(a,c))return a;if(a.parentNode)return n++,r(a.parentNode,c,b)}}function s(a,c,i,e){a instanceof Array||(a=[a]);!i&&"function"==typeof c&&(i=c,c="_root");var m=this.id,k=function(a){a:{var c=
k.original;if(b[m][c]){var e=a.target||a.srcElement,d,h,i={},f=h=0;n=0;for(d in b[m][c])if(b[m][c].hasOwnProperty(d)&&(h=r(e,d,j[m].element))&&g.matchesEvent(c,j[m].element,h,"_root"==d,a))n++,b[m][c][d].match=h,i[n]=b[m][c][d];a.stopPropagation=function(){a.cancelBubble=!0};for(h=0;h<=n;h++)if(i[h])for(f=0;f<i[h].length;f++){if(!1===i[h][f].call(i[h].match,a)){g.cancel(a);break a}if(a.cancelBubble)break a}}}},l;for(l=0;l<a.length;l++)if(k.original=a[l],(!b[this.id]||!b[this.id][a[l]])&&g.addEvent(this,
a[l],k),e){var d=a[l],f=c,p=i;if(!p&&!f)b[this.id][d]={};else if(p)for(var q=0;q<b[this.id][d][f].length;q++){if(b[this.id][d][f][q]===p){b[this.id][d][f].pop(q,1);break}}else delete b[this.id][d][f]}else d=a[l],f=c,p=i,b[this.id]||(b[this.id]={}),b[this.id][d]||(b[this.id][d]={}),b[this.id][d][f]||(b[this.id][d][f]=[]),b[this.id][d][f].push(p);return this}function g(a){if(!(this instanceof g)){for(var c in j)if(j[c].element===a)return j[c];k++;j[k]=new g(a,k);return j[k]}this.element=a;this.id=k}
var e,n=0,k=0,b={},j={};g.prototype.on=function(a,c,b){return s.call(this,a,c,b)};g.prototype.off=function(a,c,b){return s.call(this,a,c,b,!0)};g.matchesSelector=function(){};g.cancel=function(a){a.preventDefault();a.stopPropagation()};g.addEvent=function(a,b,e){a.element.addEventListener(b,e,"blur"==b||"focus"==b)};g.matchesEvent=function(){return!0};window.Gator=g})();

/* gator v1.1.1 gator-legacy.js*/
(function(b){var d=b.addEvent;b.addEvent=function(a,c,b){if(a.element.addEventListener)return d(a,c,b);"focus"==c&&(c="focusin");"blur"==c&&(c="focusout");a.element.attachEvent("on"+c,b)};b.matchesSelector=function(a){return"."===a.charAt(0)?-1<(" "+this.className+" ").indexOf(" "+a.slice(1)+" "):"#"===a.charAt(0)?this.id===a.slice(1):this.tagName===a.toUpperCase()};b.cancel=function(a){a.preventDefault&&a.preventDefault();a.stopPropagation&&a.stopPropagation();a.returnValue=!1;a.cancelBubble=!0}})(window.Gator);
`

Gator = window.Gator
window.Gator = _prevGator ? undefined # don't leak gator outside pigeon

#
# shims
#

if !Array.prototype.forEach
  Array.prototype.forEach = (fn, scope) ->
    for that, i in this
      fn.call scope, that, i, this


class Logger
  # All of the logging commands to support
  CMDS = ['log', 'info', 'warn', 'error']

  # Override this with your own logic to determine if we
  # are running in debug mode. Return true if you want
  # debug messages to show in the console.
  isDev = ->
    return true if Pigeon.debug
    return true if location.hash.search("pigeondebug=yxcvbnm") > -1
    false

  constructor: ->
    for name in CMDS
      # Generate the logging function
      @[name] = do (name) ->
        ->
          # If debugging is enabled and this browser has a console
          if isDev() and window.console?
            window.console[name].apply(console, arguments)

    # Alias debug() to log()
    @debug = @log

Log = new Logger()

Pigeon = root.Pigeon =
  BASE_URL: null

  supported: ->
    return false if typeof document.querySelectorAll == "undefined"
    return false if typeof Array.prototype.forEach == "undefined"
    true

  findSurveysOnPage: ->
    surveys = []
    elements = document.querySelectorAll '.pigeon'
    Array.prototype.forEach.call elements, (element) ->
      surveyId = element.getAttribute 'data-pigeon-surveyid'
      Log.debug "foundSurvey", surveyId
      return true unless surveyId
      surveys.push Pigeon.embedSurvey(surveyId, element)
    surveys

  embedSurvey: (surveyId, element) ->
    Log.debug "embedSurvey, ", surveyId, element
    Pigeon.loadStyles()
    new PigeonSurvey(surveyId, element)

  loadStyles: ->
    return false if document.getElementById('pigeon-css') != null
    link = document.createElement('link')
    link.id = 'pigeon-css'
    link.rel  = 'stylesheet'
    link.type = 'text/css'
    link.href = Pigeon.BASE_URL + 'css/pigeon.css'
    link.media = 'all'
    document.getElementsByTagName("head")[0].appendChild(link)

  useJSONP: ->
    parseUrl = (url) ->
      a = document.createElement 'a'
      a.href = url
      return {
        protocol: a.protocol.replace(':','')
        host: a.hostname
        port: a.port
        query: a.search
        path: a.pathname.replace(/^([^\/])/,'/$1')
        relative: (a.href.match(/tps?:\/\/[^\/]+(.+)/) || [0, ''])[1]
      }

    pageUrl = parseUrl location.href
    baseUrl = parseUrl Pigeon.BASE_URL

    if pageUrl.protocol == baseUrl.protocol and
       pageUrl.host == baseUrl.host and
       pageUrl.port == baseUrl.port
      return false

    true

class PigeonSurvey
  constructor: (surveyId, element) ->
    # two attributes for accessing the survey element.
    # fix this when dropping zepto/jquery/ender
    @el = element # pure dom element
    @element = $(element) # zepto/jquery/ender-wrapped element
    @id = surveyId
    @questions = []
    @questionsById = {}
    @userKey = @generateUserKey()
    @parseAttributes()
    @loadData()
    @currentQuestion = 0
    @firstResultSent = false
    @answers = {}

  generateUserKey: ->
    f = ->
      Math.floor(Math.random() * 0x10000).toString(16)
    t = (new Date()).getTime().toString(16)
    "u-#{f()}#{t}#{f()}"

  parseAttributes: ->
    @pageKey = @el.getAttribute('data-pigeon-pagekey') ? ""
    @userData = @el.getAttribute('data-pigeon-userdata') ? ""

  loadData: ->
    # give the page a change to hide placeholder content and display a loading indicator
    @element.addClass "pigeon-loading"
    Log.debug "load data"
    $.getJSON Pigeon.BASE_URL + 'api/survey/' + @id + ( if Pigeon.useJSONP() then '?callback=?' else '' ), (data) =>
      return false if typeof data is "undefined" or data is null
      @parse data
      @element.removeClass("pigeon-loading").addClass("pigeon-loaded")
      @display()

  parse: (data) ->
    Log.debug "parse", data
    @name = data.name if data.name?
    @questions = for questionData in data.questions
      PigeonQuestion.createFromObject(questionData)
    for question in @questions
      @questionsById[question.id] = question

  display: ->
    question = @questions[@currentQuestion]
    Log.debug "display", question
    questionEl = document.createElement "div"
    questionEl.innerHTML = '' + question.toHTML()
    Gator(questionEl).on 'click', '.pigeon-answer', @answerClicked
    Gator(questionEl).on 'click', '.pigeon-next', @nextClicked
    question.bind questionEl
    Log.debug "questionEl", questionEl
    @element.empty()
    @element.append @progressBar()
    @element.append questionEl

  displayNextQuestion: ->
    Log.debug "next!", @currentQuestion, @questions.length
    if @currentQuestion + 1 > @questions.length - 1
      # no questions anymore, we're done here.
      @displayThanks()
    else
      @currentQuestion++
      @display()

  displayThanks: ->
    @element.addClass("pigeon-done")
    @element.html """
                  <div class='pigeon-thanks'>
                    <p>Vielen Dank für dein Feedback!</p>
                  </div>
                  """

  progressBar: ->
    html = "<div class='pigeon-progress' title='#{@currentQuestion+1}/#{@questions.length+1}'>"
    for num in [0..@questions.length]
      classes = ["pigeon-progress-indicator"]
      classes.push "pigeon-progress-done" if num < @currentQuestion
      classes.push "pigeon-progress-current" if num == @currentQuestion
      html += "<div class='#{classes.join(' ')}'></div>"
    html += "</div>"
    html

  nextClicked: (event) =>
    el = event.target || event.srcElement
    $el = $(el)

    name = el.name
    questionId = parseInt(name.replace(/pigeon-next-(.*)/, '$1'))

    if questionId != @questions[@currentQuestion].id
      throw "Got an old event"

    question = @questionsById[questionId]

    if typeof question == "undefined"
      throw "Question not found"

    if question.type != "multiple" && question.type != "multiplefree"
      @answers[questionId] = []
    else
      @answers[questionId] ?= []

    wrapper = $el.parents('.pigeon-answers')
    _self = this
    lastAnswerEl = null
    wrapper.find('.pigeon-answer').each -> # thin arrow, we need the `this`
      # parameters are unuseable, zepto&jquery have el, id - but ender uses id, el. aaargh!
      answerEl = this
      if (question.type != 'multiple' && question.type != "multiplefree") || answerEl["checked"]
        _self.answers[questionId].push answerEl.value # contains answerId or user-entered-text
      if question.type == "multiplefree" && answerEl["type"] == "text"
        value = ""
        value = lastAnswerEl.value if lastAnswerEl != null && lastAnswerEl["checked"] == true # set answer-text only if checkbox is checked
        _self.answers[questionId].push value # contains user-entered-text
      lastAnswerEl = answerEl

    # send
    Log.debug "pushed answers for questionId #{questionId}:", @answers[questionId]
    @sendResult questionId

  answerClicked: (event) =>
    el = event.target || event.srcElement

    Log.debug "answerClicked", el, event

    name = el.name
    questionId = parseInt(name.replace(/pigeon-answer-(.*)/, '$1'))

    if questionId != @questions[@currentQuestion].id
      throw "Got an old event"

    question = @questionsById[questionId]

    if typeof question == "undefined"
      throw "Question not found"

    if question.hasNextButton? == true
      Log.debug "next button available, ignoring this click"
      return

    answer = el.value # contains answerId or user-entered-text

    if question.type != "multiple"
      @answers[questionId] = []
    else
      @answers[questionId] ?= []

    @answers[questionId].push answer.toString()

    Log.debug "pushed answer for questionId #{questionId}:", @answers[questionId]

    @sendResult questionId

  sendData: (method, url, data) ->
    if typeof $.ajax.compat != "undefined"
      fn = $.ajax.compat # for ender/reqwest
    else
      fn = $.ajax
    fn {
      type: method.toUpperCase()
      url: url
      data: JSON.stringify(data)
      contentType: "application/json; charset=utf-8" # we're sending json, not a <form>
      dataType: 'json'
      success: @sendResultSuccess
      error: @sendResultError
    }

  sendFirstResult: (answerData) ->
    Log.debug "sendFirstResult"
    data = {
      # survey_id: @id,
      page_key: @pageKey,
      user_key: @userKey,
      user_data: @userData,
      answers: [answerData]
    }
    @sendData 'PUT', Pigeon.BASE_URL + 'api/survey/' + @id + '/answers', data

  sendResult: (questionId) ->
    answerData = {
      question_id: questionId,
      values: @answers[questionId]
    }
    Log.debug "sendResult", answerData
    unless @firstResultSent
      return @sendFirstResult answerData

    data = {
      answers: [answerData]
    }
    @sendData 'POST', Pigeon.BASE_URL + 'api/survey/' + @id + '/answers/' + @userAnswerSetId, data

  sendResultSuccess: (data) =>
    Log.debug "gotSuccess", data
    # TODO save answerid
    @userAnswerSetId = data.id
    @firstResultSent = true
    @displayNextQuestion()

  sendResultError: (xhr, type) =>
    Log.debug "gotError", xhr, type
    @element.addClass("pigeon-fail")
    @element.html """
                  <div class='pigeon-error'>
                    <p>Danke für dein Feedback, aber unser Server konnte es wegen eines internen Fehlers leider nicht annehmen.</p>
                  </div>
                  """

class PigeonQuestion
  constructor: (options) ->
    @id = options.id
    # @type gets set by subclass
    @title = options.title if options.title?
    @text = options.text
    @answers = options.answers if options.answers?

  toHTML: ->
    throw "Use the Subclass!"

  wrapHTML: (answersHTML) ->
    Log.debug "wrapHTML", @, @type
    """
    <div class='pigeon-question pigeon-question-#{@type}'>
      <!-- #{@title} -->
      <p class='pigeon-question-text'>#{@text}</p>
      <div class='pigeon-answers pigeon-answers-#{@type}'>
        #{answersHTML}
      </div>
    </div>
    """

  bind: (el) ->
    # nothing

PigeonQuestion.createFromObject = (obj) ->
  throw "A Question needs a type" unless obj.type

  qst = switch obj.type
    when "free"     then new PigeonFreeQuestion(obj)
    when "bool"     then new PigeonBoolQuestion(obj)
    when "choice"   then new PigeonChoiceQuestion(obj)
    when "multiple" then new PigeonMultipleQuestion(obj)
    when "multiplefree" then new PigeonMultipleFreeQuestion(obj)
    else throw "QuestionType #{obj.type} unknown"

  return qst


# Question Types

class PigeonFreeQuestion extends PigeonQuestion
  @::type = "free"
  @::hasNextButton = true

  toHTML: ->
    html = "<input type='text' name='pigeon-answer-#{@id}' class='pigeon-answer' value='' />"
    # Freeform-Questions need a submit button so the user can access the next question
    html += "<button name='pigeon-next-#{@id}' class='pigeon-next'>Weiter</button>"
    @wrapHTML html

class PigeonBoolQuestion extends PigeonQuestion
  @::type = "bool"

  constructor: (options) ->
    if options.answers?.length != 2
      throw "Boolean Questions must contain only two answers."
    super options

  toHTML: ->
    html = ""
    for answer in @answers
      html += "<button name='pigeon-answer-#{@id}' class='pigeon-answer' value='#{answer.id}'>#{answer.text}</button>"
    @wrapHTML html

class PigeonChoiceQuestion extends PigeonQuestion
  @::type = "choice"

  toHTML: ->
    html = ""
    for answer in @answers
      html += "<label for='pigeon-answer-#{@id}-#{answer.id}'>
       <input type='radio' id='pigeon-answer-#{@id}-#{answer.id}' class='pigeon-answer' name='pigeon-answer-#{@id}' value='#{answer.id}' />
       #{answer.text}
       </label>"
    @wrapHTML html

class PigeonMultipleQuestion extends PigeonQuestion
  @::type = "multiple"
  @::hasNextButton = true

  toHTML: ->
    html = ""
    for answer in @answers
      html += "<label for='pigeon-answer-#{@id}-#{answer.id}'>
        <input type='checkbox' id='pigeon-answer-#{@id}-#{answer.id}' class='pigeon-answer' name='pigeon-answer-#{@id}' value='#{answer.id}' />
        #{answer.text}
        </label>"
    # Multiple Checkbox Questions need a submit button so the user can access the next question
    html += "<button name='pigeon-next-#{@id}' class='pigeon-next'>Weiter</button>"
    @wrapHTML html

class PigeonMultipleFreeQuestion extends PigeonQuestion
  @::type = "multiplefree"
  @::hasNextButton = true

  toHTML: ->
    html = ""
    i = 0
    for answer in @answers
      html += "<label for='pigeon-answer-#{@id}-#{answer.id}'>
        <input type='checkbox' id='pigeon-answer-#{@id}-#{answer.id}' class='pigeon-answer' name='pigeon-answer-#{@id}' value='#{answer.id}' />
        #{answer.text}
        </label>"
      if @answers.length == ++i
        html += "<div class='pigeon-answers-free'>
          <input type='text' id='pigeon-answer-#{@id}-#{answer.id}-free' class='pigeon-answer' name='pigeon-answer-#{@id}-free' value=''/>
          </div>"
    # Multiple Checkbox + Freetext Questions need a submit button so the user can access the next question
    html += "<button name='pigeon-next-#{@id}' class='pigeon-next'>Weiter</button>"
    @wrapHTML html

  checkedValue: (event) ->
    el = event.target || event.srcElement

    Log.debug "freetextField focused/blurred", el, event

    id = el.id
    checkboxEl = document.getElementById(id.replace(/-free$/, ''))
    if checkboxEl?
      checkboxEl["checked"] = (event.type == "focus" || el.value != "")

  bind: (el) ->
    Gator(el).on 'focus', '.pigeon-answers-free input', @checkedValue
    Gator(el).on 'blur', '.pigeon-answers-free input', @checkedValue
    super el

unless Pigeon.BASE_URL
  ((name) ->
    scripts = document.getElementsByTagName("script")
    i = scripts.length - 1
    length = name.length

    while i >= 0
      src = scripts[i].src
      l = src.length

      # set baseurl here
      Pigeon.BASE_URL = src.substr(0, l - length)  if src.substr(l - length) is name
      --i
  ) "js/pigeon.js"

# aaand run!
Pigeon.supported() && Pigeon.findSurveysOnPage()