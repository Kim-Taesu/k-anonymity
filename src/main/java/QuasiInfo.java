import java.util.HashMap;
import java.util.Map;

public class QuasiInfo {
    private String quasiName; // 준식별자 정보
    private Integer maxLevel; // 준식별자 최고 분류 레벨
    private Integer maxValue; // 준식별자 최대 값
    private Integer minValue; // 준식별자 최소 값
    private Integer weight; // 준식별자 가중치
    private Map<Integer, Integer> range = new HashMap<>();

    public Integer getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(Integer maxLevel) {
        this.maxLevel = maxLevel;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public void setQuasiName(String quasiName) {
        this.quasiName = quasiName;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public void setRange(Integer key, Integer value) {
        range.put(key, value);
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "QuasiInfo{" +
                "quasiName='" + quasiName + '\'' +
                ", maxLevel=" + maxLevel +
                ", maxValue=" + maxValue +
                ", minValue=" + minValue +
                ", weight=" + weight +
                ", range=" + range +
                '}';
    }
}
