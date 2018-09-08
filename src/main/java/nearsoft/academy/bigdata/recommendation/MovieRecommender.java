package nearsoft.academy.bigdata.recommendation;


import com.sun.org.apache.bcel.internal.generic.RET;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static javax.imageio.ImageIO.getCacheDirectory;


public class MovieRecommender {

    //private final GenericUserBasedRecommender recommender;
    private String source;
    private HashMap<String, Integer> products = new HashMap<>();
    private HashMap<String, Integer> users = new HashMap<>();



    MovieRecommender(String source) throws IOException, TasteException {

        this.source = source;
        //DataModel model = new FileDataModel(new File(generateCvs()));
        //UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        //UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        //recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
    }


    public static void main(String[] args) {
        MovieRecommender recommender = null;
        try {
            recommender = new MovieRecommender("tinyMovies.txt");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TasteException e) {
            e.printStackTrace();
        }
        recommender.generateCvs();
    }

    private String generateCvs(){

        // start timer
        long startTime = System.currentTimeMillis();

        // declaramos algunas necesidades
        ClassLoader classLoader = getClass().getClassLoader();
        String productId = null, userId = null, score = null;
        String line;
        int count = 0, product = 0, user = 0;
        boolean reading = false, face2 = false, store = false;

        try{
            //tomamos el file
            BufferedReader reader = new BufferedReader(new FileReader(classLoader.getResource(source).getFile()));

            File csv = new File(getCacheDirectory(), "results.csv");
            if(csv.exists()){
                csv.delete();
                csv.createNewFile();
            }else {
                csv.createNewFile();
            }

            Writer writer = new BufferedWriter(new FileWriter(csv));

            while ((line = reader.readLine()) != null){

                if(!reading){

                    if(line.contains("product/productId")){

                        reading = true;
                        productId = getValue(line);

                        if(!products.containsKey(productId)){
                            products.put(productId, products.size());
                        }
                        product = products.get(productId);

                    }
                }else if(!face2){

                    if(line.contains("review/userId")){
                        face2 = true;
                        userId = getValue(line);

                        if(!users.containsKey(userId)){
                            users.put(userId,users.size());
                        }
                        user = users.get(userId);
                        reader.readLine();
                        reader.readLine();
                    }

                }else {

                    if(line.contains("review/score")){
                        store = true;
                        score = getValue(line);
                        reader.readLine();
                        reader.readLine();
                        reader.readLine();
                        reader.readLine();
                    }
                }

                if(store){

                    String x = String.valueOf(product);
                    String y = String.valueOf(user);

                    writer.append(x);
                    writer.append(",");
                    writer.append(y);
                    writer.append(",");
                    writer.append(score);
                    writer.append("\n");


                    //writer.write("-"); //this line is here just because there is a bug and idk why but this fix it
                   // writer.write(product+","+user+","+score+"\n");
                    writer.write(x+"-"+y+"\n");
                    //writer.write(product+","+user+"\n");



                    count++;
                    reading = false;
                    face2 = false;
                    store =false;
                    System.out.println(product+","+user+","+score);
                }
            }

            long endTime = System.currentTimeMillis();
            System.out.println(csv.getAbsolutePath());
            System.out.println("That took " + (endTime - startTime) + " milliseconds");
            return csv.getAbsolutePath();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

            return "";
    }

    private String getValue(String line){
        return line.substring(line.indexOf(" ") + 1);
    }


    public int getTotalReviews() {
        return 1;
    }

    public int getTotalProducts() {
        return 1;
    }

    public int getTotalUsers() {
        return 1;
    }

    public List<String> getRecommendationsForUser(String a141HP4LYPWMSR) {
        return null;
    }
}
