package com.locationbasedrecommendation.RecomenderApp2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;



public class App extends JFrame
{
	JButton button;
	JLabel label1, label2, label3;
	JTextField textField1, textField2, textField3;
	JTextArea textArea;
    

	public static void main( String[] args ) throws Exception
    {
		File file = new File("checkList.txt");
		if (!file.exists()){
			inputFile();
			fixZero();
		}	
		
		new App();
      
    }
	
	public App(){
		this.setSize(600,300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Location Based Recommendation");
		
		JPanel myPanel = new JPanel();
		
	
		label1 = new JLabel("UserID");
		myPanel.add(label1);
		
		textField1 = new JTextField("",5);
		myPanel.add(textField1);
		
		label2 = new JLabel("Recommendation Count");
		myPanel.add(label2);
		
		textField2 = new JTextField("",5);
		myPanel.add(textField2);
		
		label3 = new JLabel("Treshold");
		myPanel.add(label3);
		
		textField3= new JTextField("",5);
		myPanel.add(textField3);
		
		button = new JButton("Calculate");
		
		ListenForButton buttonListener = new ListenForButton();
		button.addActionListener(buttonListener);
		
		myPanel.add(button);
		
		textArea = new JTextArea(13,30);
		
		textArea.setLineWrap(true);
		JScrollPane scrollBar = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textArea.setWrapStyleWord(true);

		myPanel.add(scrollBar);
		
		
		this.add(myPanel);
		this.setVisible(true);
		textField1.requestFocus();
		
	}
    
	
	class ListenForButton implements ActionListener{
		
		DataModel model;
		UserSimilarity similarity;
		UserNeighborhood neighborhood;
		UserBasedRecommender recommender;
		List<RecommendedItem> recommendations;
		int userID, countRec;
		double treshold;
		
		JFrame frame = new JFrame("");

		public void actionPerformed(ActionEvent e) {
			
			if(e.getSource()==button){
				
				textArea.setText(null);
				userID = Integer.parseInt(textField1.getText());
				countRec = Integer.parseInt(textField2.getText());
				treshold = Double.parseDouble(textField3.getText());
				
				
				try {
					model = new FileDataModel(new File("checkList1.txt"));
					similarity = new PearsonCorrelationSimilarity(model);
			        neighborhood = new ThresholdUserNeighborhood(treshold, similarity, model);
			        recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
			        recommendations = recommender.recommend(userID, countRec);
			        
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (TasteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
			if(!recommendations.isEmpty()){
				String itemm=null;
				for(RecommendedItem item: recommendations){
					System.out.println(item);
					itemm = item.toString();
					String space = ",";
					String[] words = itemm.split(space);
					words[0] = words[0].substring(21, words[0].length());
					words[1] = words[1].substring(7, words[1].length()-1);
					textArea.append("Location ID: "+words[0]+" -- "+ "value: "+words[1]+"\n");
				}
			}else{
				System.out.println("no recommendations");
				textArea.append("There are not recommendations");
			}
			
		}

	}
	
    public static void inputFile(){
    	
    	String fileName = "Gowalla_totalCheckins.txt";
    	
    	File file = new File(fileName);
    	
    	
    	try{
    		FileWriter checkListFile = new FileWriter("checkList.txt");
    		BufferedWriter writer = new BufferedWriter(checkListFile);
    		
			Scanner input = new Scanner(file);
			HashMap<String, Integer> hm = new HashMap<String,Integer>();
			
			String userID = "0";
			
			while(input.hasNextLine()){
				
				String line = input.nextLine();
				String space = "\t";
				String[] words = line.split(space);
				Integer value = (Integer) hm.get(words[4]);
		
				if (words[0].equals(userID)){
					if (value==null){
						hm.put(words[4], 1);
					}else{
						value++;
						hm.put(words[4], value);
					}
				}else{
					Set set = hm.entrySet();
					Iterator i = set.iterator();
					
					while(i.hasNext()){
						Map.Entry me = (Map.Entry)i.next();
						writer.write(userID+","+me.getKey().toString()+","+me.getValue().toString());
						writer.newLine();
					}
					
					
					hm.clear();
					userID = words[0];
					}
				}
			Set set = hm.entrySet();
			Iterator i = set.iterator();
			
			while(i.hasNext()){
				Map.Entry me = (Map.Entry)i.next();
				writer.write(userID+","+me.getKey().toString()+","+me.getValue().toString());
				writer.newLine();
			}
			
			
			hm.clear();
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	
    	
    }
    
    public static void fixZero(){
    	String fileName = "checkList.txt";
    	
    	File file = new File(fileName);
    	

    		FileWriter checkListFile;
			try {
				checkListFile = new FileWriter("checkList1.txt");
				BufferedWriter writer = new BufferedWriter(checkListFile);
	    		
				Scanner input = new Scanner(file);
				
				while(input.hasNextLine()){
					
					String line = input.nextLine();
					String space = ",";
					String[] words = line.split(space);
					int a = 7 - words[1].length();
					for(int i=0;i<a;i++){
						words[1]="0"+words[1];
					}
					
					String line1 = words[0]+","+words[1]+","+words[2];
					writer.write(line1);
					writer.newLine();
					
				}
				writer.close();
				
				
			}catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
			
    }
}
