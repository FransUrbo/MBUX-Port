
@file:Suppress("unused", "FunctionName")
package com.rndash.mbheadunit.nativeCan.canC
import com.rndash.mbheadunit.CanFrame // AUTO GEN
import com.rndash.mbheadunit.nativeCan.CanBusNative // AUTO GEN

/**
 *   Generated by db_converter.py
 *   Object for EWM_230h (ID 0x0230)
**/

object EWM_230h {

    	/** Gets gear selector lever position (only NAG) **/
	fun get_whc() : WHC = when(CanBusNative.getECUParameterC(CanCAddrs.EWM_230h, 4, 4)) {
		 5 -> WHC.D
		 6 -> WHC.N
		 7 -> WHC.R
		 8 -> WHC.P
		 9 -> WHC.PLUS
		 10 -> WHC.MINUS
		 11 -> WHC.N_ZW_D
		 12 -> WHC.R_ZW_N
		 13 -> WHC.P_ZW_R
		 15 -> WHC.SNV
		 else -> throw Exception("Invalid raw value for WHC")
	}
	
	/** Sets gear selector lever position (only NAG) **/
	fun set_whc(f: CanFrame, p: WHC) = CanBusNative.setFrameParameter(f, 4, 4, p.raw)
	
	/** Gets Locking magnet energized **/
	fun get_locking() : Boolean = CanBusNative.getECUParameterC(CanCAddrs.EWM_230h, 3, 1) != 0
	
	/** Sets Locking magnet energized **/
	fun set_locking(f: CanFrame, p: Boolean) = CanBusNative.setFrameParameter(f, 3, 1, if(p) 1 else 0)
	
	/** Gets kickdown **/
	fun get_kd() : Boolean = CanBusNative.getECUParameterC(CanCAddrs.EWM_230h, 2, 1) != 0
	
	/** Sets kickdown **/
	fun set_kd(f: CanFrame, p: Boolean) = CanBusNative.setFrameParameter(f, 2, 1, if(p) 1 else 0)
	
	/** Gets Drive program button pressed **/
	fun get_fpt() : Boolean = CanBusNative.getECUParameterC(CanCAddrs.EWM_230h, 1, 1) != 0
	
	/** Sets Drive program button pressed **/
	fun set_fpt(f: CanFrame, p: Boolean) = CanBusNative.setFrameParameter(f, 1, 1, if(p) 1 else 0)
	
	/** Gets drive program **/
	fun get_w_s() : Boolean = CanBusNative.getECUParameterC(CanCAddrs.EWM_230h, 0, 1) != 0
	
	/** Sets drive program **/
	fun set_w_s(f: CanFrame, p: Boolean) = CanBusNative.setFrameParameter(f, 0, 1, if(p) 1 else 0)
	
	
}