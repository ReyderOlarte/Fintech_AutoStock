package com.autoStock;

import java.sql.SQLException;

import com.autoStock.internal.ApplicationStates;
import com.autoStock.internal.Global;
import com.autoStock.internal.Global.Mode;
import com.autoStock.menu.MenuController;
import com.autoStock.menu.MenuDefinitions.MenuStructures;
import com.autoStock.menu.MenuLauncher;

/**
 * @author Kevin Kowalewski
 *
 */
public class MainClient {	
	public static void main(String[] args) throws SQLException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Co.println("******** Welcome to autoStock ********\n");
		
		MenuController menuController = new MenuController();
		if (args.length == 0){menuController.displayMenu(MenuStructures.menu_main); ApplicationStates.shutdown();}
		MenuStructures menuStructure = menuController.getRelatedMenu(args);
		menuController.handleMenuStructure(menuStructure, args);
		
		if (menuStructure == MenuStructures.menu_main_backtest 
			|| menuStructure == MenuStructures.menu_main_clustered_backtest 
			|| menuStructure == MenuStructures.menu_main_clustered_backtest_client
			|| menuStructure == MenuStructures.menu_main_test
			|| menuStructure == MenuStructures.menu_main_indicator_test
			|| menuStructure == MenuStructures.menu_quick_command){
				Co.println("--> Skipped TWS initialization");
				ApplicationStates.startup(Mode.client_skip_tws);
		}else{
			ApplicationStates.startup(Mode.client);
		}
		
		new MenuLauncher().launchDisplay(menuStructure);
		
		while (Global.callbackLock.isLocked()){
			try{Thread.sleep(1*250);}catch(InterruptedException e){return;}
		}
		
		Co.println("\n******** Finished ********");
		
		System.exit(0);
	}
}
