import Jama.Matrix;

import java.util.*;

public class RecommenderSystem {

    /*
    1.先給矩陣去中心化--減去均值，但數值為0的元素不減均值
    2.計算Pearson Similarity
    3.求出最接近的user或item
    4.看看最接近的users中有沒有為那個item評分
    5.按有評分的similarity * 評分的總和除以similarity的總和則為預測評分
     */

    public static void main(String[] args) {
        double[][] data = {{2,1,5,4,3,0},{0,2,0,3,5,4},{5,0,4,1,4,2},{2,3,4,5,0,0},{0,4,1,0,3,2}};
        double[][] input = new Matrix(data).transpose().getArray();
        double[] row_mean = new double[input.length];

        for(int i = 0; i<input.length; i++){ //計算均值
            int index = 0;
            for(int j =0;j<input[i].length;j++){
                if(input[i][j] !=0){
                    index++;
                    row_mean[i]+=input[i][j];
                }
            }
            row_mean[i]=row_mean[i]/index;
        }

        for(int i = 0; i<input.length; i++){ //去均值
            for(int j =0;j<input[i].length;j++){
                if(input[i][j] !=0){
                    input[i][j]=input[i][j]-row_mean[i];
                }
            }
        }

        double[][] similarity = new double[input.length][input.length];

        for(int i = 0; i< input.length; i++){ //計算pearson similarity
            for(int j = 0; j<input.length; j++){
                double tempA = 0;
                double tempB = 0;
                double sum=0;
                for(int k=0;k<input[0].length;k++){
                    sum+=input[i][k]*input[j][k];
                    tempA += Math.pow(input[i][k],2);
                    tempB += Math.pow(input[j][k],2);
                }
                double total = Math.sqrt(tempA*tempB);
                similarity[i][j] = sum/total;
            }
        }
        TreeMap<Double,Integer> treeMap = new TreeMap<>(new Comparator<Double>() { //自動排序
            @Override
            public int compare(Double o1, Double o2) {
                if(o1<o2) return 1;
                else if (o1>o2) return -1;
                else return 0;
            }
        });

        for(int i=0;i< similarity[4].length;i++){ //只取Movie E 的Similarity
            if(similarity[4][i]!=1){
                treeMap.put(similarity[4][i], i); //different
            }
        }

        for(int i = 0; i < similarity.length;i++){
            System.out.println(Arrays.toString(similarity[i]));
        }


        double predict_score=0;
        double sim_total=0;
        Set<Double> key = treeMap.keySet();

        int n = 0;
        for (Double sim: key){
            if(n<2){
                System.out.println("Nearest neighbors: " + "Movie " + treeMap.get(sim));
                predict_score+=sim * input[treeMap.get(sim)][3];
                n++;
                if(input[treeMap.get(sim)][3] != 0){
                    sim_total += sim;
                }
            }
        }
        predict_score = (predict_score/sim_total) + row_mean[3];
        System.out.println("Predicted Score: " + predict_score);

    }
}
