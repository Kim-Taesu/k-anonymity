import java.io.*;
import java.util.*;

public class JavaAnonymity {

    static ArrayList<ArrayList<Integer>> quasiData = new ArrayList<>();
    static ArrayList<String> sensiData = new ArrayList<>();
    static Map<Integer, QuasiInfo> taxonomyInfo = new HashMap<>();
    static ArrayList<String> taxonomyCases = new ArrayList<>();
    static Map<String, Integer> finalResult = new HashMap<>();

    public static void main(String[] args) throws IOException {
        // 결과 폴더 초기화
        final File file = new File(JavaConfig.JAVA_RESULT_DIR);
        final File[] files = file.listFiles();
        for (File value : files) {
            value.delete();
        }


        // 분류 트리 생성 및 초기화
        setUpTaxonomy();

        // 분류 트리 경우의 수 계산
        int[] tmp = new int[taxonomyInfo.size()];
        findTaxonomyCase(tmp, 0);

        // 원본 데이터 read
        readData();

        // 모든 분류 트리 경우의 수에 대해 k-익명화 수행
        for (String item : taxonomyCases) {
            kAnonymity(item);
        }

        // K-익명화 조건을 만족하는 경우의 수와 점수 내림차순 출력
        final List<String> keys = sortByValue(finalResult);
        for (String key : keys) {
            System.out.println("taxonomy case: " + key + "\t" + "score: " + finalResult.get(key));
        }
    }


    public static List<String> sortByValue(final Map<String, Integer> map) {
        List<String> list = new ArrayList<>(map.keySet());
        list.sort((o1, o2) -> {
            Object v1 = map.get(o1);
            Object v2 = map.get(o2);
            return ((Comparable) v2).compareTo(v1);
        });
        return list;
    }


    private static void readData() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(JavaConfig.RAW_DATA_PATH)));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            final String[] split = line.split(JavaConfig.DELIMITER);
            int index = 0;
            ArrayList<Integer> quasiDataTmp = new ArrayList<>();
            for (; index < split.length - 1; index++) {
                quasiDataTmp.add(Integer.parseInt(split[index]));
            }
            quasiData.add(quasiDataTmp);
            sensiData.add(split[index]);
        }
    }

    private static void kAnonymity(String item) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        Map<String, Integer> kCheck = new HashMap<>();
        int itemScore = 0;

        int[] range = new int[item.length()];
        for (int index = 0; index < item.length(); index++) {
            int tmp = Integer.parseInt(String.valueOf(item.charAt(index)));
            itemScore += tmp * (taxonomyInfo.size() + 1 - taxonomyInfo.get(index).getWeight());
            if (tmp == 0) {
                range[index] = -1;
            } else {
                final Integer maxValue = taxonomyInfo.get(index).getMaxValue();
                range[index] = maxValue / tmp;
            }
        }


        int sensiIndex = 0;
        for (ArrayList<Integer> quasiItem : quasiData) {
            ArrayList<String> resultTmp = new ArrayList<>();
            for (int index = 0; index < quasiItem.size(); index++) {
                final int rangeValue = range[index];
                final Integer realValue = quasiItem.get(index);
                if (rangeValue == -1) {
                    resultTmp.add(realValue.toString());
                } else {
                    final int from = realValue / rangeValue;
                    final int fromRest = realValue % rangeValue;
                    final String noiseValue;
                    if (from != 0 && fromRest == 0) {
                        noiseValue = (from - 1) * rangeValue + "-" + from * rangeValue;
                    } else {
                        final int to = from + 1;
                        noiseValue = from * rangeValue + "-" + to * rangeValue;
                    }
                    resultTmp.add(noiseValue);
                }
            }
            final String noiseValue = resultTmp.toString();
            if (kCheck.containsKey(noiseValue)) {
                kCheck.put(noiseValue, kCheck.get(noiseValue) + 1);
            } else {
                kCheck.put(noiseValue, 1);
            }
            resultTmp.add(sensiData.get(sensiIndex++));
            result.add(resultTmp);
        }

        final Iterator<Integer> values = kCheck.values().iterator();
        int minValue = Integer.MAX_VALUE;
        while (values.hasNext()) {
            final Integer next = values.next();
            minValue = Math.min(next, minValue);
        }
        // k-익명화 조건 만족
        if (minValue != Integer.MAX_VALUE && minValue > JavaConfig.K_VALUE) {
            File file = new File(JavaConfig.JAVA_RESULT_DIR + itemScore + "_" + item + JavaConfig.EXTENSION);
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                for (ArrayList<String> strings : result) {
                    final String s = strings.toString();
                    final String substring = s.substring(1, s.length() - 1).replace(" ", "");
                    bw.write(substring + "\n");
                }
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finalResult.put(item, itemScore);
        }
    }

    private static void setUpTaxonomy() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(JavaConfig.TAXONOMY_INFO_PATH)));
        String line;
        Integer id = 0;
        while ((line = bufferedReader.readLine()) != null) {
            final String[] split = line.split(JavaConfig.DELIMITER);
            final String quasiName = split[0];
            final int maxLevel = Integer.parseInt(split[1]);
            final int minValue = Integer.parseInt(split[2]);
            final int maxValue = Integer.parseInt(split[3]);
            final int weight = Integer.parseInt(split[4]);
            QuasiInfo quasiInfo = new QuasiInfo();
            quasiInfo.setQuasiName(quasiName);
            quasiInfo.setMaxLevel(maxLevel);
            quasiInfo.setMinValue(minValue);
            quasiInfo.setMaxValue(maxValue);
            quasiInfo.setWeight(weight);
            quasiInfo.setRange(0, 0);
            for (int level = 1; level <= maxLevel; level++) {
                final int step = maxValue / level;
                quasiInfo.setRange(level, step);
            }
            taxonomyInfo.put(id++, quasiInfo);
        }
        bufferedReader.close();
    }

    private static void findTaxonomyCase(int[] tmp, int index) {
        if (index == tmp.length) {
            StringBuilder result = new StringBuilder();
            for (int i : tmp) {
                result.append(i);
            }
            taxonomyCases.add(result.toString());
            return;
        }

        for (int i = 0; i <= taxonomyInfo.get(index).getMaxLevel(); i++) {
            tmp[index] += i;
            findTaxonomyCase(tmp, index + 1);
            tmp[index] -= i;
        }
    }
}

