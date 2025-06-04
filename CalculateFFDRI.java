
public class CalculateFFDRI {

    static GetWeather gw = new GetWeather();

	private double RNE;
    private double humidity= gw.getHumidity();;
    private double rainfall= gw.getRainfall();
    private double avgWind = 3.2; //
    private double maxTemp = 32.0;
    private double effHum = 25.0;
	
	 // FFDRI 최종 계산
    public double calculate(int fmi, String direction) {
        double dwi = calculateDWI(maxTemp, effHum, avgWind);
        double tmi = getTMI(direction);
        double weight = 0.85;// 5월 고정
        return (7 * dwi + 1.5 * fmi + 1.5 * tmi) * weight;
    }

    public double getTMI(String direction) {
	    	return switch (direction) {
	        case "E" -> 1.5;
	        case "N", "W" -> 2.5;
	        case "SE", "S" -> 4.0;
	        case "NW", "NE" -> 4.5;
	        default -> 5.0;
    	};
    }
    
    // RNE 계산 (강우량)
    public double getRNE(double rainfall) {

        if (rainfall < 1.0) return 1.0;
        else if (rainfall < 5.0) return 0.5;
        else if (rainfall < 10.0) return 0.4;
        else if (rainfall < 50.0) return 0.4;
        else if (rainfall < 100.0) return 0.2;
        else return 0.1;
    }
    
    //DWI 계산
    public double calculateDWI(double maxTemp,double effectiveHumidity, double avgWind) {
        RNE = getRNE(rainfall);
    	double expVal = 2.706 + (0.088 * maxTemp) - (0.055 * humidity)
                - (0.023 * effectiveHumidity) - (0.104 * avgWind);

    	double preDWI = (1.0 / (1.0 + Math.exp(expVal))) - 1.0;
    	
    	double reclassified = Math.max(0, Math.min(preDWI, 1));
        return reclassified * RNE;

    }
    
    
}
