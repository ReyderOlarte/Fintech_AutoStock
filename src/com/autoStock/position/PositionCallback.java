package 	com.autoStock.position;

import com.autoStock.Co;
import com.autoStock.account.BasicAccount;
import com.autoStock.account.TransactionFees;
import com.autoStock.order.OrderDefinitions.OrderMode;
import com.autoStock.order.OrderDefinitions.OrderType;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.trading.types.Order;
import com.autoStock.trading.types.Position;

/**
 * @author Kevin Kowalewski
 *
 */
public class PositionCallback {
	public static void setPositionSuccess(Position position) throws IllegalAccessError {
		if (position.positionType == PositionType.position_long_entry){position.positionType = PositionType.position_long;}
		else if (position.positionType == PositionType.position_short_entry){position.positionType = PositionType.position_short;}
		else if (position.positionType == PositionType.position_long_exit){position.positionType = PositionType.position_long_exited;}
		else if (position.positionType == PositionType.position_short_exit){position.positionType = PositionType.position_short_exited;}
		else throw new UnsupportedOperationException("No condition matched PositionType: " + position.positionType.name());
	}

	public static void affectBankBalance(Order order, OrderMode orderMode, BasicAccount basicAccount, Position position){
		if (orderMode == OrderMode.mode_exchange){
			Co.println("Affecting bank balance: " + order.symbol.name + ", " + order.getOrderValue().valueFilled);
		}
		if (order.orderType == OrderType.order_long || order.orderType == OrderType.order_short){
			if (orderMode == OrderMode.mode_exchange){
				Co.println("--> Changing bank balance: " + order.orderType.name() + ", " + (-1 * order.getOrderValue().valueFilled) + ", " + TransactionFees.getTransactionCost(order.getUnitsFilled(), order.getOrderValue().unitPriceFilled));
			}
			
			basicAccount.modifyBalance(-1 * order.getOrderValue().valueFilled, TransactionFees.getTransactionCost(order.getUnitsFilled(), order.getOrderValue().unitPriceFilled));
		}else if (order.orderType == OrderType.order_long_exited){
			if (position.getPositionUtils().getOrderUnitsFilled() != order.getUnitsRequested()){
				throw new IllegalStateException("Units don't add up");
			}
			if (orderMode == OrderMode.mode_exchange){
				Co.println("--> Changing bank balance: " + order.orderType.name() + ", " + (order.getOrderValue().valueFilled) + ", " + TransactionFees.getTransactionCost(order.getUnitsFilled(), order.getOrderValue().unitPriceFilled));
			}
			
			basicAccount.modifyBalance(order.getOrderValue().valueFilled, TransactionFees.getTransactionCost(order.getUnitsFilled(), order.getOrderValue().unitPriceFilled));
		}else if (order.orderType == OrderType.order_short_exited){
			if (position.getPositionUtils().getOrderUnitsFilled() != order.getUnitsRequested()){
				throw new IllegalStateException("Units don't add up");
			}
//			Co.println("--> Exit short: " + order.getOrderValue().valueFilled + ", " + order.getUnitsFilled() + ", " + position.getPositionValue().valueFilled);
			
			basicAccount.modifyBalance(position.getPositionValue().valueFilled - (order.getOrderValue().valueFilled - position.getPositionValue().valueFilled), TransactionFees.getTransactionCost(order.getUnitsFilled(), order.getOrderValue().unitPriceFilled));
		}else{
			throw new IllegalStateException("Order type is: " + order.orderType.name());
		}
	}
}
