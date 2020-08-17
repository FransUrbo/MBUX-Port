
@file:Suppress("unused", "FunctionName")
package com.rndash.mbheadunit.nativeCan.canB
import com.rndash.mbheadunit.CanFrame // AUTO GEN
import com.rndash.mbheadunit.nativeCan.CanBusNative // AUTO GEN

/**
 *   Generated by db_converter.py
 *   Object for TP_AGW_TELEAID6 (ID 0x03E3)
**/

object TP_AGW_TELEAID6 {

    	/** Gets communication AGW to TELEAID **/
	fun get_tp_agw_teleaid() : Int = CanBusNative.getECUParameterB(CanBAddrs.TP_AGW_TELEAID6, 0, 64)
	
	/** Sets communication AGW to TELEAID **/
	fun set_tp_agw_teleaid(f: CanFrame, p: Int) = CanBusNative.setFrameParameter(f, 0, 64, p)
	
	
}