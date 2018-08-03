package io.github.vrchatapi.mobile.logging;

public enum LogLevel {
	
	FATAL(0),
	ERROR(1),
	WARN(2),
	INFO(3),
	
	;
	
	private int level;
	
	private LogLevel(int level) {
		this.level = level;
	}
	
	public boolean shouldLog(LogLevel current) {
		return current.level >= level; 
	}
	
}
