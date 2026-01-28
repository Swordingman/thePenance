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
/*    */ 
/*    */ 
/*    */ public enum BlendMode
/*    */ {
/* 36 */   normal(770, 1, 771),
/* 37 */   additive(770, 1, 1),
/* 38 */   multiply(774, 774, 771),
/* 39 */   screen(1, 1, 769);
/*    */   
/*    */   int source;
/*    */   int sourcePMA;
/*    */   
/*    */   BlendMode(int source, int sourcePremultipledAlpha, int dest) {
/* 45 */     this.source = source;
/* 46 */     this.sourcePMA = sourcePremultipledAlpha;
/* 47 */     this.dest = dest;
/*    */   }
/*    */   int dest; public static final BlendMode[] values;
/*    */   public int getSource(boolean premultipliedAlpha) {
/* 51 */     return premultipliedAlpha ? this.sourcePMA : this.source;
/*    */   }
/*    */   
/*    */   public int getDest() {
/* 55 */     return this.dest;
/*    */   }
/*    */   static {
/* 58 */     values = values();
/*    */   }
/*    */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\BlendMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */