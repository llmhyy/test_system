package control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import paper.Iterator;
import paper.Page;
import paper.Record;
import paper.Survey;
import paper.Test;
import question.ChoiceQuestion;
import question.DecideQuestion;
import question.EssayQuestion;
import question.ItemQuestion;
import question.MapQuestion;
import question.PromptQuestion;
import question.Question;
import question.RankQuestion;
import question.ShortEssayQuestion;
import anwser.Answer;
import anwser.ChoiceAnswer;
import anwser.DecideAnswer;
import anwser.MapAnswer;
import anwser.RankAnswer;
import anwser.TextAnswer;

public class IO {
	SAXBuilder builder = new SAXBuilder();
	
	public List<String>[] readAllPages(){
		InputStream file;
		Element root = null;
		try {
			file = new FileInputStream("xml/pageInfo.xml");
			Document document = builder.build(file);//获得文档对象
			root = document.getRootElement();//获得根节点

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Element> pageList = root.getChildren("pageName");
		List<String>[] pageName = new List[2];
		pageName[0] = new LinkedList<String>();
		pageName[1] = new LinkedList<String>();
		for(int i=0; i<pageList.size(); i++){
			if(pageList.get(i).getAttributeValue("type").equals("test")){
				pageName[1].add(pageList.get(i).getText());
			}else{
				pageName[0].add(pageList.get(i).getText());
			}
		}
		return pageName;
	}
	
	public void writeAllPages(List<String>[] pageName){
		
		Element root = new Element("totalInfo");
		for(int i=0; i<pageName[0].size(); i++){
			Element page = new Element("pageName");
			page.addContent(pageName[0].get(i));
			page.setAttribute("type", "survey");
			root.addContent(page);
		}
		for(int i=0; i<pageName[1].size(); i++){
			Element page = new Element("pageName");
			page.addContent(pageName[1].get(i));
			page.setAttribute("type", "test");
			root.addContent(page);
		}
		Document doc=new Document(root);  
		 try {
			FileOutputStream out=new FileOutputStream("xml/pageInfo.xml");
			XMLOutputter outputter = new XMLOutputter();  
	        Format f = Format.getPrettyFormat();  
	        outputter.setFormat(f);  
	        outputter.output(doc, out);  
	        out.close();  
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public Page readPage(String pageName){
		InputStream file;
		Element root = null;
		try {
			file = new FileInputStream("xml/"+pageName+".xml");
			Document document = builder.build(file);//获得文档对象
			root = document.getRootElement();//获得根节点

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Page page;
		if(root.getAttribute("type").getValue().equals("test")){
			Test test = new Test();
			test.setTotalScore(Integer.parseInt(root.getChildText("score")));
			page = test;
			page.setType("test");
		}else{
			page = new Survey();
			page.setType("survey");
		}
		page.setPageName(pageName);
		System.out.println(page.getPageName());
		Element questions =  root.getChild("questions");
		List<Element> questionList = questions.getChildren();
		for(int i=0; i<questionList.size(); i++){
			Element question = questionList.get(i);
			int type = Integer.parseInt(question.getAttributeValue("type"));
			Question q = null;
			switch(type){
			case 0: 
				DecideQuestion decide = new DecideQuestion();
				q = this.readDecideQuestion(question, decide);
				break;
			case 1: 
				ChoiceQuestion choice = new ChoiceQuestion();
				q = this.readChoiceQuestion(question, choice);
				break;
			case 2: 
				ShortEssayQuestion text = new ShortEssayQuestion();
				q = this.readTextQuestion(question, text);
				break;
			case 3: 
				EssayQuestion essay = new EssayQuestion();
				q = this.readEssayQuestion(question, essay);
				break;
			case 4: 
				RankQuestion rank = new RankQuestion();
				q = this.readRankQuestion(question, rank);
				break;
			case 5: 
				MapQuestion map = new MapQuestion();
				q = this.readMapQuestion(question, map);
				break;
			}
			page.addQuestion(q);
		}
		return page;
	}
	
	public Question readDecideQuestion(Element question, DecideQuestion decide){
		decide.setPrompt(question.getChildText("prompt"));
		decide.setScore(Integer.parseInt(question.getChildText("score")));
		if(question.getAttributeValue("answer").equals("1")){
			decide.setAnswer(question.getChildText("answer"));
		}
		return decide;
	}
	
	public Question readChoiceQuestion(Element question, ChoiceQuestion choice){
		choice.setPrompt(question.getChildText("prompt"));
		choice.setScore(Integer.parseInt(question.getChildText("score")));
		List<Element> items = question.getChild("items").getChildren();
		for(int i=0; i<items.size(); i++){
			Element item = items.get(i);
			choice.setItem(item.getText());
		}
		if(question.getAttributeValue("answer").equals("1")){
			choice.setAnswer(question.getChildText("answer"));
		}
		return choice;
	}
	
	public Question readTextQuestion(Element question, ShortEssayQuestion text){
		text.setPrompt(question.getChildText("prompt"));
		text.setScore(Integer.parseInt(question.getChildText("score")));
		if(question.getAttributeValue("answer").equals("1")){
			text.setAnswer(question.getChildText("answer"));
		}
		return text;
	}
	
	public Question readEssayQuestion(Element question, EssayQuestion essay){
		essay.setPrompt(question.getChildText("prompt"));
		essay.setAnswer("");
		return essay;
	}
	
	public Question readRankQuestion(Element question, RankQuestion rank){
		rank.setPrompt(question.getChildText("prompt"));
		rank.setScore(Integer.parseInt(question.getChildText("score")));
		List<Element> items = question.getChild("items").getChildren();
		for(int i=0; i<items.size(); i++){
			rank.setItem(items.get(i).getText());
		}
		System.out.println(question.getChildText("answer"));
		if(question.getAttributeValue("answer").equals("1")){
			rank.setAnswer(question.getChildText("answer"));
		}
		return rank;
	}
	
	public Question readMapQuestion(Element question, MapQuestion map){
		map.setPrompt(question.getChildText("prompt"));
		Element side1 = (Element) question.getChild("side1");
		List<Element> sideList1 = side1.getChildren();
		map.setSide(1);
		for(int j=0; j<sideList1.size(); j++){
			map.setItem(sideList1.get(j).getText());
		}
		Element side2 = (Element) question.getChild("side2");
		List<Element> sideList2 = side2.getChildren();
		map.setSide(2);
		for(int j=0; j<sideList2.size(); j++){
			map.setItem(sideList2.get(j).getText());
		}
		if(question.getAttributeValue("isScore").equals("1")){
			map.setScore(Integer.parseInt(question.getChildText("score")));
		}
		if(question.getAttributeValue("answer").equals("1")){
			map.setAnswer(question.getChildText("answer"));						
		}
		return map;
	}
	
	public void writePage(Page page){
		Element root = new Element("Page");
		root.setAttribute("type", page.getType());
		root.addContent(new Element("pageName").setText(page.getPageName()));
		if(page.getType().equals("test")){
			root.addContent(new Element("score").setText(((Test)page).getTotalScore()+""));
		}
		
		List<Question> questionList = page.getQuestionList();
		Element questions = new Element("questions");
		for(int i=0; i<questionList.size(); i++){
			Question question = questionList.get(i);
			Element qe = null;
			switch(question.getType()){
			case 0: qe = savePromptQuestion((PromptQuestion)question);break;
			case 1: qe = saveItemQuestion((ItemQuestion)question);break;
			case 2: qe = savePromptQuestion((PromptQuestion)question);break;
			case 3: qe = saveEssayQuestion((EssayQuestion)question);break;
			case 4: qe = saveItemQuestion((ItemQuestion)question);break;
			case 5: qe = saveMapQuestion((MapQuestion)question);break;
			}
			questions.addContent(qe);			
		}
		root.addContent(questions);
		Document doc=new Document(root);  
		 try {
			FileOutputStream out=new FileOutputStream("xml/"+page.getPageName()+".xml");
			XMLOutputter outputter = new XMLOutputter();  
	        Format f = Format.getPrettyFormat();  
	        outputter.setFormat(f);  
	        outputter.output(doc, out);  
	        out.close();  
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public Element savePromptQuestion(PromptQuestion promptQuestion){
		Element ret = new Element("question");
		ret.setAttribute("type", promptQuestion.getType()+"");
		ret.setAttribute("isScore", "1");
		int anwser = 1;
		if(promptQuestion.getAnswer() == null)
			anwser = 0;
		ret.setAttribute("answer", anwser+"");
		Element prompt = new Element("prompt");
		prompt.setText(promptQuestion.getPrompt());
		ret.addContent(prompt);
		if(promptQuestion.getAnswer() != null){
			Answer an = promptQuestion.getAnswer();
			Element answerElement = new Element("answer");
			answerElement.setText(an.writeAnswer());
			ret.addContent(answerElement);
		}
		
		ret.addContent(new Element("score").setText(promptQuestion.getScore()+""));
		return ret;
	}
	
	public Element saveItemQuestion(ItemQuestion item){
		Element ret = new Element("question");
		ret.setAttribute("type", item.getType()+"");
		ret.setAttribute("isScore", "1");
		int anwser = 1;
		if(item.getAnswer() == null)
			anwser = 0;
		ret.setAttribute("answer", anwser+"");
		Element prompt = new Element("prompt");
		prompt.setText(item.getPrompt());
		ret.addContent(prompt);
		List<String> items = item.getItem();
		Element itemElement = new Element("items");
		for(int j=0; j<items.size(); j++){
			itemElement.addContent(new Element("item").setText(items.get(j)));
		}
		ret.addContent(itemElement);
		if(item.getAnswer() != null){
			Answer an = item.getAnswer();
			Element answerElement = new Element("answer");
			answerElement.setText(an.writeAnswer());
			ret.addContent(answerElement);
		}
		
		ret.addContent(new Element("score").setText(item.getScore()+""));
		return ret;
	}
	
	public Element saveMapQuestion(MapQuestion map){
		Element ret =new Element("question");
		ret.setAttribute("type", map.getType()+"");
		ret.setAttribute("isScore", "1");
		int anwser = 1;
		if(map.getAnswer() == null)
			anwser = 0;
		ret.setAttribute("answer", anwser+"");
		Element prompt = new Element("prompt");
		prompt.setText(map.getPrompt());
		ret.addContent(prompt);
		
		map.setSide(1);
		List<String> side1 = map.getItem();
		Element item1 = new Element("side1");
		for(int j=0; j<side1.size(); j++){
			item1.addContent(new Element("left").setText(side1.get(j)));
		}
		ret.addContent(item1);
		
		map.setSide(2);
		List<String> side2 = map.getItem();
		Element item2 = new Element("side2");
		for(int j=0; j<side2.size(); j++){
			item2.addContent(new Element("right").setText(side2.get(j)));
		}
		ret.addContent(item2);
		if(map.getAnswer() != null){
			Answer an = map.getAnswer();
			Element answerElement = new Element("answer");
			answerElement.setText(an.writeAnswer());
			ret.addContent(answerElement);
		}
		
		ret.addContent(new Element("score").setText(map.getScore()+""));
		return ret;
	}
	
	public Element saveEssayQuestion(EssayQuestion question){
		Element ret =new Element("question");
		ret.setAttribute("type", question.getType()+"");
		ret.setAttribute("isScore", "0");
		ret.setAttribute("answer", "0");
		Element prompt = new Element("prompt");
		prompt.setText(question.getPrompt());
		question.setAnswer(null);
		ret.addContent(prompt);
		return ret;
	}
	
	public List<String> readRecordInfo(String pageName){
		InputStream file;
		Element root = null;
		try {
			File recordFile = new File("xml/record/"+pageName+"-recordInfo.xml");
			if(!recordFile.exists()){
				return new LinkedList<String>();
			}
			file = new FileInputStream("xml/record/"+pageName+"-recordInfo.xml");
			
			Document document = builder.build(file);//获得文档对象
			root = document.getRootElement();//获得根节点

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Element> pageList = root.getChildren("record");
		List<String> records = new LinkedList<String>();
		for(int i=0; i<pageList.size(); i++){
			records.add(pageList.get(i).getText()); 
		}
		return records;
	}
	
	public void writeReordInfo(String pageName, List<String> recordName){
		Element root = new Element("Records");
		for(int i=0; i<recordName.size(); i++){
			Element record = new Element("record");
			record.setText(recordName.get(i));
			root.addContent(record);
		}
		
		Document doc=new Document(root);  
		 try {
			FileOutputStream out=new FileOutputStream("xml/record/"+pageName+"-recordInfo.xml");
			XMLOutputter outputter = new XMLOutputter();  
	        Format f = Format.getPrettyFormat();  
	        outputter.setFormat(f);  
	        outputter.output(doc, out);  
	        out.close();  
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	public void writeRecord(String recordName, Record record){
		Element root = new Element("Record");
		Element personName = new Element("PersonName");
		personName.setText(record.getPersonName());
		root.addContent(personName);
		Element score = new Element("score");
		score.setText(record.getScore()+"");
		Element answers = new Element("answers");
		root.addContent(score);
		Iterator<Answer> iterator = record.iterator();
		
		while(iterator.hasNext()){
			Answer answer = iterator.next();
			Element answerElement = new Element("answer");
			answerElement.setAttribute("type", answer.getType()+"");
			answerElement.setText(answer.writeAnswer());
			answers.addContent(answerElement);
		}
		
		root.addContent(answers);
		
		Document doc=new Document(root);  
		 try {
			FileOutputStream out=new FileOutputStream("xml/record/"+recordName+".xml");
			XMLOutputter outputter = new XMLOutputter();  
	        Format f = Format.getPrettyFormat();  
	        outputter.setFormat(f);  
	        outputter.output(doc, out);  
	        out.close();  
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public Record readRecord(String recordName){
		
		InputStream file;
		Element root = null;
		try {
			file = new FileInputStream("xml/record/"+recordName+".xml");
			Document document = builder.build(file);//获得文档对象
			root = document.getRootElement();//获得根节点

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Record record = new Record();
		record.setPersonName(root.getChildText("PersonName"));
		record.addScore(Integer.parseInt(root.getChildText("score")));
		Element answers = root.getChild("answers");
		List<Element> answerList = answers.getChildren();
		System.out.println(answerList.size());
		for(int i=0; i<answerList.size(); i++){
			Element answer = answerList.get(i);
			int type = Integer.parseInt(answer.getAttributeValue("type"));
			switch(type){
			case 0: DecideAnswer decide = new DecideAnswer();
					decide.setAnswer(answer.getText());
					record.addAnwser(decide);
					break;
			case 1: ChoiceAnswer choice = new ChoiceAnswer();
					choice.setAnswer(answer.getText());
					record.addAnwser(choice);
					break;
			case 2: TextAnswer text = new TextAnswer();
					text.setAnswer(answer.getText());
					record.addAnwser(text);
					break;
			case 4: RankAnswer rank = new RankAnswer();
					rank.setAnswer(answer.getText());
					record.addAnwser(rank);
					break;
			case 5: MapAnswer map = new MapAnswer();
					map.setAnswer(answer.getText());
					record.addAnwser(map);
					break;
			}
		}
		return record;
	}
	
}
