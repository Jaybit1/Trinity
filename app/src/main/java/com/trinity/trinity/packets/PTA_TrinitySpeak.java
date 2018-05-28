package com.trinity.trinity.packets;

import packets.Packet;

public class PTA_TrinitySpeak extends Packet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3370239038350343032L;

	private String text;
	
	public PTA_TrinitySpeak(String text) {
		this.text = text;

	}

	public String getText() {
		return text;
	}

}
