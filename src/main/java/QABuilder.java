import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class QABuilder {

	/**
	 * It reads from file to get questions and answers. Questions are marked
	 * with 'Q:'. Answers are marked with 'A:' .
	 * 
	 * @param args
	 *            - command line arguments.
	 */
	public static void main(String[] args) {
		if (args.length == 1) {
			StringBuilder questionBuilder = new StringBuilder();
			StringBuilder answerBuilder = new StringBuilder();

			PrintWriter questionAnswer = null;
			try (BufferedReader bis = new BufferedReader(new FileReader(args[0]))) {
				String eachLine = bis.readLine();

				questionAnswer = new PrintWriter(new File("QuestionAnswer.js"));
				questionAnswer.println("exports.handler = (event, context, callback) => {");
				questionAnswer.println();
				questionAnswer.println();
				questionAnswer.println("var questions = [");
				boolean first = true;

				// Whether answer is continuing or not.
				// Starts with question so by default it will be false.
				// Normally questions are one liner.
				// During processing answers will have multiple lines.
				boolean answer = false;

				long questionCount = 0;
				long answerCount = 0;

				List<String> questionList = new ArrayList<>();
				List<String> answerList = new ArrayList<>();

				while (eachLine != null) {
					// each line will be either question or answer
					// Question line will start with "Q:"
					eachLine = eachLine.trim();
					eachLine = eachLine.replace("'", "\\'");

					if (eachLine.length() > 0) {
						if (eachLine.startsWith("Q:")) {

							eachLine = eachLine.replace("Q:", "");
							eachLine = eachLine.replace("Q.", "");
							eachLine = eachLine.replace("?", "");
							eachLine = eachLine.toLowerCase();

							eachLine = eachLine.trim();

							if (answerBuilder.length() > 0) {
								answerList.add(answerBuilder.toString());
								answerBuilder.delete(0, answerBuilder.length());
								answerCount++;
							}

							questionBuilder.append(eachLine);
							answer = false;

						} else if (eachLine.startsWith("A:") || eachLine.startsWith("A.") || answer) {
							// Answer line will start with "A:"
							eachLine = eachLine.replace("A:", "");
							eachLine = eachLine.replace("A.", "");
							eachLine = eachLine.replace("'", "\'");
							eachLine = eachLine.trim();

							if (eachLine.startsWith("http")) {
								eachLine = " your answer is a link to a webpage " + eachLine;
							}
							if (questionBuilder.length() > 0) {
								addQuestion(questionAnswer, questionBuilder.toString(), first, questionList);
								questionBuilder.delete(0, questionBuilder.length());
								questionCount++;
								first = false;
							}
							answerBuilder.append(eachLine);
							answer = true;
						} else {
							System.out.println(eachLine);
						}
					}

					eachLine = bis.readLine();
					if (eachLine == null) {
						if (answerBuilder.length() > 0) {
							answerList.add(answerBuilder.toString());
							answerBuilder.delete(0, answerBuilder.length());
							answerCount++;
						}
					}
				}

				questionAnswer.println("];");

				questionAnswer.println("var answers = [");
				for (int i = 0; i < answerList.size(); i++) {
					if (i == 0) {
						questionAnswer.println("'" + answerList.get(i) + "'");
					} else {
						questionAnswer.println(",'" + answerList.get(i) + "'");
					}
				}

				questionAnswer.println("];");
				questionAnswer.println();
				questionAnswer.println();
				questionAnswer.println("var asked_question = event.currentIntent.slots.slotOne.trim().toLowerCase();");
				questionAnswer.println("console.log(\"asked_question :\",asked_question);");
				questionAnswer.println("var reply = \"Sorry, did not find answer to your question\";");
				questionAnswer.println("var number_of_questions = questions.length;");
				questionAnswer.println("var number_of_answers = answers.length;");
				questionAnswer.println("");
				questionAnswer.println("");
				questionAnswer.println("for (var i = 0; i < number_of_questions; i++) {");
				questionAnswer.println("    if (asked_question === questions[i]){");
				questionAnswer.println("        reply = answers[i];");
				questionAnswer.println("        break;");
				questionAnswer.println("     }");
				questionAnswer.println("}");
				questionAnswer.println();
				questionAnswer.println();
				questionAnswer.println("console.log(\"reply :\",reply);");
				questionAnswer.println("callback(null, {");
				questionAnswer.println("    \"dialogAction\": {");
				questionAnswer.println("      \"type\": \"Close\",");
				questionAnswer.println("      \"fulfillmentState\": \"Fulfilled\", // <-- Required");
				questionAnswer.println("      \"message\": {");
				questionAnswer.println("        \"contentType\": \"PlainText\",");
				questionAnswer.println("       \"content\": reply");
				questionAnswer.println("      }");
				questionAnswer.println("    }} )");
				questionAnswer.println("};");

				System.out.println("Number of questions :" + questionList.size() + " Question count :" + questionCount);
				System.out.println("Number of answers :" + answerList.size() + " Answer count : " + answerCount);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (questionAnswer != null) {
					questionAnswer.close();
				}
			}
		} else {
			System.err.println("QABuilder <input file name>");
		}

	}

	/**
	 * 
	 * @param questionAnswer
	 * @param eachLine
	 * @param first
	 * @param questionList
	 */
	private static void addQuestion(PrintWriter questionAnswer, String eachLine, boolean first,
			List<String> questionList) {
		if (first) {
			first = false;
			questionAnswer.println("'" + eachLine + "'");
		} else {
			questionAnswer.println(",'" + eachLine + "'");
		}
		questionList.add(eachLine);

	}

}
