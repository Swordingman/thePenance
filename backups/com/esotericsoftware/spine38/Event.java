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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Event
/*     */ {
/*     */   private final EventData data;
/*     */   int intValue;
/*     */   float floatValue;
/*     */   String stringValue;
/*     */   float volume;
/*     */   float balance;
/*     */   final float time;
/*     */   
/*     */   public Event(float time, EventData data) {
/*  50 */     if (data == null) throw new IllegalArgumentException("data cannot be null."); 
/*  51 */     this.time = time;
/*  52 */     this.data = data;
/*     */   }
/*     */   
/*     */   public int getInt() {
/*  56 */     return this.intValue;
/*     */   }
/*     */   
/*     */   public void setInt(int intValue) {
/*  60 */     this.intValue = intValue;
/*     */   }
/*     */   
/*     */   public float getFloat() {
/*  64 */     return this.floatValue;
/*     */   }
/*     */   
/*     */   public void setFloat(float floatValue) {
/*  68 */     this.floatValue = floatValue;
/*     */   }
/*     */   
/*     */   public String getString() {
/*  72 */     return this.stringValue;
/*     */   }
/*     */   
/*     */   public void setString(String stringValue) {
/*  76 */     if (stringValue == null) throw new IllegalArgumentException("stringValue cannot be null."); 
/*  77 */     this.stringValue = stringValue;
/*     */   }
/*     */   
/*     */   public float getVolume() {
/*  81 */     return this.volume;
/*     */   }
/*     */   
/*     */   public void setVolume(float volume) {
/*  85 */     this.volume = volume;
/*     */   }
/*     */   
/*     */   public float getBalance() {
/*  89 */     return this.balance;
/*     */   }
/*     */   
/*     */   public void setBalance(float balance) {
/*  93 */     this.balance = balance;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getTime() {
/*  98 */     return this.time;
/*     */   }
/*     */ 
/*     */   
/*     */   public EventData getData() {
/* 103 */     return this.data;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 107 */     return this.data.name;
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\Event.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */