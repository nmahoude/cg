package cgfx.components;

import cgfx.wrappers.EvaluationWrapper;
import cgfx.wrappers.GameWrapper;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;

public class EvalChart extends BorderPane {

	private LineChart chart;
	private EvaluationWrapper wrapper;
	
	public EvalChart(EvaluationWrapper wrapper) {
		this.wrapper = wrapper;
		final NumberAxis xAxis = new NumberAxis();
	    final NumberAxis yAxis = new NumberAxis();
        
        xAxis.setLabel("turns");
        
        yAxis.setLabel("values");

        chart = new LineChart(xAxis, yAxis);
        this.setCenter(chart);
        for (String name : wrapper.names()) {
        	XYChart.Series serie = new XYChart.Series();
        	serie.setName(name);
            chart.getData().add(serie);

        }
        
	}

	public void update(int index, GameWrapper stateWrapper) {
		
		wrapper.update(stateWrapper);
		for (int i=0;i<wrapper.count();i++) {
			((XYChart.Series)chart.getData().get(i)).getData().add(new XYChart.Data(index,wrapper.value(i)));
		}
	}
}
