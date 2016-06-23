package com.autoStock.encog;

import java.io.File;
import java.io.FileInputStream;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.PersistBasicNetwork;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import com.autoStock.Co;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalPointType;
import com.autoStock.signal.SignalPoint;

/**
 * @author Kevin Kowalewski
 *
 */
public class TestEncogTactic {
	private BasicNetwork basicNetwork;
	
	public TestEncogTactic() {
		try {
			PersistBasicNetwork persistBasicNetwork = new PersistBasicNetwork();
			basicNetwork = (BasicNetwork) persistBasicNetwork.read(new FileInputStream(new File("../EncogTest/encog.file")));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public SignalPoint getSignalPoint(int[] cciWindow){
		SignalPoint signalPoint = new SignalPoint();
		NormalizedField normalizedFieldForCCI = new NormalizedField(NormalizationAction.Normalize, "CCI", 50, -50, 1, -1);
		
		MLData input = new BasicMLData(3);
        input.setData(0, normalizedFieldForCCI.normalize(cciWindow[0]));
        input.setData(1, normalizedFieldForCCI.normalize(cciWindow[1]));
        input.setData(2, normalizedFieldForCCI.normalize(cciWindow[2]));
        
        Co.println("--> CCI WINDOW: + " + cciWindow[0] + ", " + cciWindow[1] + ", " + cciWindow[2]);
        
        MLData output = basicNetwork.compute(input);
        
        double valueForEntry = output.getData(0);
        double valueForExit = output.getData(1);
        
        if (valueForEntry > 0.9){
        	signalPoint.signalPointType = SignalPointType.long_entry;
        	signalPoint.signalMetricType = SignalMetricType.none;
        }else if (valueForExit > 0.9){
        	signalPoint.signalPointType = SignalPointType.long_exit;
        	signalPoint.signalMetricType = SignalMetricType.none;
        }
        
        return signalPoint;
	}
}
