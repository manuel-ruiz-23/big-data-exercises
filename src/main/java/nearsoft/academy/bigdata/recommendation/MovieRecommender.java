package nearsoft.academy.bigdata.recommendation;


//import com.sun.org.apache.bcel.internal.generic.RET;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.imageio.ImageIO.getCacheDirectory;

public class MovieRecommender {

    private final GenericUserBasedRecommender recommender;
    private String source;
    private HashMap<String, Integer> products = new HashMap<>();
    private HashMap<String, Integer> users = new HashMap<>();
    private int totalReviews = 0;

    /*
    public static void main(String[] args) {

        try {
            MovieRecommender reocomendation = new MovieRecommender("movies.txt");

            reocomendation.getRecommendationsForUser("A141HP4LYPWMSR");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TasteException e) {
            e.printStackTrace();
        }
    }
    */

    MovieRecommender(String source) throws IOException, TasteException {
        this.source = source;
        DataModel model = new FileDataModel(new File(generateCvs()));
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
    }

    private String generateCvs(){

        // start timer
        long startTime = System.currentTimeMillis();

        // declaramos algunas necesidades
        ClassLoader classLoader = getClass().getClassLoader();
        String productId = null, userId = null, score = null;
        String line;
        int product = 0, user = 0;
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
                    writer.write(user+","+product+","+score+"\n");
                    totalReviews++;
                    reading = false;
                    face2 = false;
                    store =false;
                }
            }
            writer.close();
            long endTime = System.currentTimeMillis();
            //path to the generated csv
            //System.out.println(csv.getAbsolutePath());

            System.out.println("creating the csv file took: " + (endTime - startTime) + " milliseconds");
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
        return totalReviews;
    }

    public int getTotalProducts() {
        return products.size();
    }

    public int getTotalUsers() {
        return users.size();
    }

    public List<String> getRecommendationsForUser(String userId) throws TasteException {

        List<String> recomendedMovies = new ArrayList<>();

        List<RecommendedItem> recommendations = recommender.recommend(users.get(userId), 3);

        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation.getItemID());

            for(Map.Entry<String, Integer> entry : products.entrySet()){
                if(entry.getValue() == recommendation.getItemID()) {
                    System.out.println(entry);
                    recomendedMovies.add(entry.getKey());
                }
            }
        }

        return recomendedMovies;
    }
}
