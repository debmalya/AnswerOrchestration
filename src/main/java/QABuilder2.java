import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class QABuilder2 {

	public static void main(String[] args) {
		if (args.length == 1) {
			PrintWriter questionAnswer = null;
			try (BufferedReader bis = new BufferedReader(new FileReader(args[0]))) {
				String eachLine = bis.readLine();
				questionAnswer = new PrintWriter(new File("QuestionAnswer.js"));
				questionAnswer.println("var questions = [");
				boolean first = true;
				List<String> questionList = new ArrayList<>();
				List<String> answerList = new ArrayList<>();
				while (eachLine != null) {
					// each line will be either question or answer
					// Question line will start with "Q:"
					eachLine = eachLine.trim();
					eachLine = eachLine.replace("'", "\'");
					
					if (eachLine.length() > 0) {
						if (eachLine.startsWith("Q:") || eachLine.startsWith("Q.")) {
							eachLine = eachLine.replace("Q:", "");
							eachLine = eachLine.replace("Q.", "");
							eachLine = eachLine.replace("?", "");
							eachLine = eachLine.toLowerCase();
							
							eachLine = eachLine.trim();
							if (first) {
								first = false;
								questionAnswer.println("'" + eachLine + "'");
							} else {
								questionAnswer.println(",'" + eachLine + "'");
							}
							questionList.add(eachLine);

						} else if (eachLine.startsWith("A:") || eachLine.startsWith("A.")) {
							// Answer line will start with "A:"
							eachLine = eachLine.replace("A:", "");
							eachLine = eachLine.replace("A.", "");
							eachLine = eachLine.replace("'", "\'");
							eachLine = eachLine.trim();
							
							if (eachLine.startsWith("http")){
								eachLine = " your answer is a link to a webpage " + eachLine;
							}
							answerList.add(eachLine);
						} else {
							System.out.println(eachLine);
						}
					}
					eachLine = bis.readLine();
				}
				
				questionAnswer.println("];");
				
				questionAnswer.println("var answers = [");
				for (int i = 0; i < answerList.size(); i++){
					if (i == 0){
						questionAnswer.println("'" + answerList.get(i) + "'");
					}else{
						questionAnswer.println(",'" + answerList.get(i) + "'");
					}
				}
				questionAnswer.println("];");
				
				System.out.println("Number of questions :" + questionList.size() );
				System.out.println("Number of answers :" + answerList.size() );
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

}
