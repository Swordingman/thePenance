/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EventData
/*     */ {
/*     */   final String name;
/*     */   int intValue;
/*     */   float floatValue;
/*     */   String stringValue;
/*     */   String audioPath;
/*     */   float volume;
/*     */   float balance;
/*     */   
/*     */   public EventData(String name) {
/*  43 */     if (name == null) throw new IllegalArgumentException("name cannot be null."); 
/*  44 */     this.name = name;
/*     */   }
/*     */   
/*     */   public int getInt() {
/*  48 */     return this.intValue;
/*     */   }
/*     */   
/*     */   public void setInt(int intValue) {
/*  52 */     this.intValue = intValue;
/*     */   }
/*     */   
/*     */   public float getFloat() {
/*  56 */     return this.floatValue;
/*     */   }
/*     */   
/*     */   public void setFloat(float floatValue) {
/*  60 */     this.floatValue = floatValue;
/*     */   }
/*     */   
/*     */   public String getString() {
/*  64 */     return this.stringValue;
/*     */   }
/*     */   
/*     */   public void setString(String stringValue) {
/*  68 */     if (stringValue == null) throw new IllegalArgumentException("stringValue cannot be null."); 
/*  69 */     this.stringValue = stringValue;
/*     */   }
/*     */   
/*     */   public String getAudioPath() {
/*  73 */     return this.audioPath;
/*     */   }
/*     */   
/*     */   public void setAudioPath(String audioPath) {
/*  77 */     if (audioPath == null) throw new IllegalArgumentException("audioPath cannot be null."); 
/*  78 */     this.audioPath = audioPath;
/*     */   }
/*     */   
/*     */   public float getVolume() {
/*  82 */     return this.volume;
/*     */   }
/*     */   
/*     */   public void setVolume(float volume) {
/*  86 */     this.volume = volume;
/*     */   }
/*     */   
/*     */   public float getBalance() {
/*  90 */     return this.balance;
/*     */   }
/*     */   
/*     */   public void setBalance(float balance) {
/*  94 */     this.balance = balance;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  99 */     return this.name;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 103 */     return this.name;
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\EventData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */