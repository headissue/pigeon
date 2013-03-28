# Create A New Question Type

On this example question type we will show you how to define your own question types.

## The Server Side

 1. `com.headissue.pigeon.survey.QuestionType`

    Define a new type like `MULTIPLE_FREE` which is meant to be "pick an answer or write your own if no answer fits".

        /**
         * The same as multiple choice plus a free text input
         */
        public static final String MULTIPLE_FREE = "multiplefree";

 2. `com.headissue.pigeon.service.AdminQuestionValidator`
    
    As we are creating a question of that type, we need to make sure, that there are at least two possible answers. Because a multiple choice question without any choice at all does not make any sense, does it? 
    We must use a different QuestionValidator as the MultipleChoice QuestionValidator.


        QuestionValidate choiceMultiple = new QuestionValidate() {
          @Override
          public void check(Question q) {
            if (q.getAnswers().size() < 2) {
              throw new BeanException(q.getType() + " question must have more then 1 answers");
            }
          }
        };
        addValidation(QuestionType.CHOICE, choiceMultiple);
        addValidation(QuestionType.MULTIPLE, choiceMultiple);
        addValidation(QuestionType.MULTIPLE_FREE, choiceMultiple); // the new question type

 3. `com.headissue.pigeon.service.AnswerTransformFactory`

    An AnswerTransform has to be created as inner class


        static class MultipleOneFreeAnswerTransform extends AnswerTransform {

          public boolean transfer(Answer _answer, UserAnswerValue _value) {
            List<Integer> _values = new ArrayList<Integer>();
            List<String> _texts = new ArrayList<String>();
            int _lastIndex = _value.getValues().size() - 1;
            for (int _index = 0; _index < _value.getValues().size(); _index++) {
              String _key = _value.getValues().get(_index);
              if (_index < _lastIndex) {
                try {
                  _values.add(Integer.parseInt(_key));
                } catch (Exception e) {
                  return false;
                }
              } else {
                _texts.add(_key);
              }
            }
            _answer.setAnswerValues(_values);
            _answer.setAnswerTexts(_texts);
            return true;
          }
        }


 4. `com.headissue.pigeon.service.QuestionAnswerTransformFactory`

    Connect the AnswerTransform with the QuestionType and you are ready to ... wait, we need to extend the client side first.


        public class QuestionAnswerTransformFactory extends AnswerTransformFactory {
          {
            AnswerTransform _oneTransform = new OneKeyAnswerTransform();
            add(QuestionType.BOOL, _oneTransform);
            add(QuestionType.CHOICE, _oneTransform);
            add(QuestionType.MULTIPLE, new IntAnswerTransform());
            add(QuestionType.FREE, new OneTextAnswerTransform());
            add(QuestionType.MULTIPLE_FREE, new MultipleOneFreeAnswerTransform());
          }
        }


## The Client Side

Create new `PigeonQuestion`-based class for your new question type
The class needs an type defined in `@::type` - it must be the same you set on the [server side in step 1](#the-server-side).

If the user can choose multiple answers in this step, you should set `@::hasNextButton` to true and add an next-button in `toHTML`.
This ensures clicks on a single answer don't get submitted immediately and the next question would be displayed.

Your class must have a `toHTML` function, `bind` is an option function that gets called after creation of the dom for this question so you could bind additional event handlers.

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
          if @answers.length == ++i # if this is the last answer, add the freetext field
            html += "<div class='pigeon-answers-free'>
              <input type='text' id='pigeon-answer-#{@id}-#{answer.id}-free' class='pigeon-answer' name='pigeon-answer-#{@id}-free' value=''/>
              </div>"
        # Multiple Checkbox + Freetext Questions need a next button so the user can access the next question
        html += "<button name='pigeon-next-#{@id}' class='pigeon-next'>Weiter</button>"
        @wrapHTML html

      checkedValue: (event) ->
        el = event.target || event.srcElement
        id = el.id
        checkboxEl = document.getElementById(id.replace(/-free$/, ''))
        if checkboxEl?
          checkboxEl["checked"] = (el.value != "")

      bind: (el) ->
        Gator(el).on 'focus', '.pigeon-answers-free input', @checkedValue
        Gator(el).on 'blur', '.pigeon-answers-free input', @checkedValue
        super el