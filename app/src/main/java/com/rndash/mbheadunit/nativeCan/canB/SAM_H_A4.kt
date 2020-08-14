package com.rndash.mbheadunit.nativeCan.canB


import com.rndash.mbheadunit.nativeCan.CanBAddrs
import com.rndash.mbheadunit.nativeCan.CanBusNative



/**
 *    GENERATED BY parse_kt.py
 *    Decoder class for ECU Frame SAM_H_A4 (ID: 0x0041)
**/
object SAM_H_A4 {

	/** Lock nut 1 (unlock) **/
	fun get_sn1_sam_h() : Boolean = getParam(1, 1) != 0

	/** ZV emergency opening **/
	fun get_zv_notoeff() : Boolean = getParam(7, 1) != 0

	override fun toString(): String {
		return """
			SN1_SAM_H: ${get_sn1_sam_h()}
			ZV_NOTOEFF: ${get_zv_notoeff()}
		""".trimIndent()
	}


	private fun getParam(o: Int, l: Int) : Int = CanBusNative.getECUParameterB(CanBAddrs.SAM_H_A4, o, l)
}