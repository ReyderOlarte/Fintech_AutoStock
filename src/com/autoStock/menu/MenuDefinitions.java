package com.autoStock.menu;

/**
 * @author Kevin Kowalewski
 *
 */
public class MenuDefinitions {
	public static enum MenuStructures {
		menu_main(new MenuArguments[]{MenuArguments.arg_none}),
		menu_main_backtest(new MenuArguments[]{MenuArguments.arg_start_date, MenuArguments.arg_end_date, MenuArguments.arg_exchange, MenuArguments.arg_symbol_array}), //MenuArguments.arg_backtest_type
		menu_main_clustered_backtest(new MenuArguments[]{MenuArguments.arg_start_date, MenuArguments.arg_end_date, MenuArguments.arg_exchange, MenuArguments.arg_symbol_array}),
		menu_main_clustered_backtest_client(new MenuArguments[]{MenuArguments.arg_none}),
		menu_main_engage(new MenuArguments[]{MenuArguments.arg_exchange}),
		menu_main_active_algorithm(new MenuArguments[]{MenuArguments.arg_exchange, MenuArguments.arg_symbol}),
		menu_main_market_filter(new MenuArguments[]{MenuArguments.arg_exchange}),
		menu_main_market_index_data(new MenuArguments[]{MenuArguments.arg_exchange, MenuArguments.arg_index}),
		menu_main_test(new MenuArguments[]{MenuArguments.arg_none}),
		menu_main_indicator_test(new MenuArguments[]{MenuArguments.arg_start_date, MenuArguments.arg_end_date, MenuArguments.arg_exchange, MenuArguments.arg_symbol}),
		menu_request_historical_prices(new MenuArguments[]{MenuArguments.arg_start_date, MenuArguments.arg_end_date, MenuArguments.arg_exchange, MenuArguments.arg_symbol, MenuArguments.arg_resolution}),
		menu_request_market_symbol_data(new MenuArguments[]{MenuArguments.arg_exchange, MenuArguments.arg_symbol}),
		menu_request_realtime_data(new MenuArguments[]{MenuArguments.arg_security_type, MenuArguments.arg_symbol}),
		menu_request_market_order(new MenuArguments[]{MenuArguments.arg_exchange, MenuArguments.arg_symbol, MenuArguments.arg_position_type, MenuArguments.arg_position_units}),
		menu_test_realtime_data(new MenuArguments[]{MenuArguments.arg_none}),
		menu_test_market_data(new MenuArguments[]{MenuArguments.arg_exchange, MenuArguments.arg_symbol}),
		menu_internal_build_database_definitions(new MenuArguments[]{MenuArguments.arg_none}),
		menu_internal_build_replay_from_file(new MenuArguments[]{MenuArguments.arg_exchange, MenuArguments.arg_file_name}), 
		menu_quick_command(new MenuArguments[]{MenuArguments.arg_command}),
		menu_main_backtest_wm(new MenuArguments[]{MenuArguments.arg_start_date, MenuArguments.arg_end_date, MenuArguments.arg_exchange, MenuArguments.arg_symbol_array}),
		menu_main_backtest_wm_dod(new MenuArguments[]{MenuArguments.arg_start_date, MenuArguments.arg_exchange, MenuArguments.arg_symbol_array}),
		menu_main_backtest_encog(new MenuArguments[]{MenuArguments.arg_start_date, MenuArguments.arg_end_date, MenuArguments.arg_exchange, MenuArguments.arg_symbol_array}),
		menu_main_generate_ideal(new MenuArguments[]{MenuArguments.arg_none}),
		menu_main_rewrite_evaluation(new MenuArguments[]{MenuArguments.arg_none}),
		;
		
		public MenuArguments[] arrayOfMenuArguments;
		
		private MenuStructures(MenuArguments[] arrayOfMenuArguments) {
			this.arrayOfMenuArguments = arrayOfMenuArguments;
		}
		
		public MenuArguments getArgument(MenuArguments menuArgument){
			for (MenuArguments tempMenuArgument : arrayOfMenuArguments){
				if (tempMenuArgument == menuArgument){
					return menuArgument;
				}
			}
			
			return null;
		}
	}
	
	public static enum MenuArguments{
		arg_none(new MenuArgumentTypes[]{MenuArgumentTypes.const_none}, ""),
		arg_restart_delay(new MenuArgumentTypes[]{MenuArgumentTypes.const_now, MenuArgumentTypes.const_safe, MenuArgumentTypes.basic_integer}, "Restart delay"),
		arg_security_type(new MenuArgumentTypes[]{MenuArgumentTypes.const_stk, MenuArgumentTypes.const_opt, MenuArgumentTypes.const_fut}, "Security Type"),
		arg_symbol(new MenuArgumentTypes[]{MenuArgumentTypes.basic_string}, "Symbol"),
		arg_index(new MenuArgumentTypes[]{MenuArgumentTypes.basic_string}, "Index"),
		arg_symbol_array(new MenuArgumentTypes[]{MenuArgumentTypes.basic_string_array}, "Symbol Array"),
		arg_start_date(new MenuArgumentTypes[]{MenuArgumentTypes.basic_date}, "Start Date"),
		arg_end_date(new MenuArgumentTypes[]{MenuArgumentTypes.basic_date}, "End Date or Period"), // MenuArgumentTypes.basic_period}
		arg_resolution(new MenuArgumentTypes[]{MenuArgumentTypes.basic_resolution}, "Reporting resolution"),
		arg_algorithm(new MenuArgumentTypes[]{MenuArgumentTypes.basic_string}, "Algorithm"),
		arg_exchange(new MenuArgumentTypes[]{MenuArgumentTypes.basic_string}, "Exchange"),
		arg_backtest_type(new MenuArgumentTypes[]{MenuArgumentTypes.basic_string}, "Backtest Type"),
		arg_position_units(new MenuArgumentTypes[]{MenuArgumentTypes.basic_string}, "Shares"),
		arg_position_type(new MenuArgumentTypes[]{MenuArgumentTypes.basic_string}, "Position Type"),
		arg_file_name(new MenuArgumentTypes[]{MenuArgumentTypes.basic_string}, "File Name"),
		arg_command(new MenuArgumentTypes[]{MenuArgumentTypes.basic_string}, "Command"),
		;
		
		public MenuArgumentTypes[] arrayOfArgumentTypes;
		public String argumentDescription;
		public String value;
		
		MenuArguments(MenuArgumentTypes[] arrayOfArgumentTypes, String argumentDescription){
			this.arrayOfArgumentTypes = arrayOfArgumentTypes;
			this.argumentDescription = argumentDescription;
		}
	}
	
	public static enum MenuArgumentTypes {
		basic_integer,
		basic_float,
		basic_string,
		basic_string_array,
		basic_date,
		basic_period,
		basic_resolution,
		const_now,
		const_safe,
		const_stk,
		const_opt,
		const_fut,
		const_none
	}
}
