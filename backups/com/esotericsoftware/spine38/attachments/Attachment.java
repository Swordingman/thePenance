/*    */ package com.esotericsoftware.spine38.attachments;
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
/*    */ public abstract class Attachment
/*    */ {
/*    */   String name;
/*    */   
/*    */   public Attachment(String name) {
/* 37 */     if (name == null) throw new IllegalArgumentException("name cannot be null."); 
/* 38 */     this.name = name;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 43 */     return this.name;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 47 */     return this.name;
/*    */   }
/*    */   
/*    */   public abstract Attachment copy();
/*    */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\attachments\Attachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */