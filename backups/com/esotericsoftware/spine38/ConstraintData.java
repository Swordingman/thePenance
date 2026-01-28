/*    */ package com.esotericsoftware.spine38;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ConstraintData
/*    */ {
/*    */   final String name;
/*    */   int order;
/*    */   boolean skinRequired;
/*    */   
/*    */   public ConstraintData(String name) {
/* 39 */     if (name == null) throw new IllegalArgumentException("name cannot be null."); 
/* 40 */     this.name = name;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 45 */     return this.name;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getOrder() {
/* 51 */     return this.order;
/*    */   }
/*    */   
/*    */   public void setOrder(int order) {
/* 55 */     this.order = order;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean getSkinRequired() {
/* 62 */     return this.skinRequired;
/*    */   }
/*    */   
/*    */   public void setSkinRequired(boolean skinRequired) {
/* 66 */     this.skinRequired = skinRequired;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 70 */     return this.name;
/*    */   }
/*    */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\ConstraintData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */