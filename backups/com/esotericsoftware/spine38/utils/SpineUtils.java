/*    */ package com.esotericsoftware.spine38.utils;
/*    */ 
/*    */ import java.lang.reflect.Array;
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
/*    */ public class SpineUtils
/*    */ {
/*    */   public static final float PI = 3.1415927F;
/*    */   public static final float PI2 = 6.2831855F;
/*    */   public static final float radiansToDegrees = 57.295776F;
/*    */   public static final float radDeg = 57.295776F;
/*    */   public static final float degreesToRadians = 0.017453292F;
/*    */   public static final float degRad = 0.017453292F;
/*    */   
/*    */   public static float cosDeg(float angle) {
/* 41 */     return (float)Math.cos((angle * 0.017453292F));
/*    */   }
/*    */   
/*    */   public static float sinDeg(float angle) {
/* 45 */     return (float)Math.sin((angle * 0.017453292F));
/*    */   }
/*    */   
/*    */   public static float cos(float angle) {
/* 49 */     return (float)Math.cos(angle);
/*    */   }
/*    */   
/*    */   public static float sin(float angle) {
/* 53 */     return (float)Math.sin(angle);
/*    */   }
/*    */   
/*    */   public static float atan2(float y, float x) {
/* 57 */     return (float)Math.atan2(y, x);
/*    */   }
/*    */   
/*    */   public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length) {
/* 61 */     if (src == null) throw new IllegalArgumentException("src cannot be null."); 
/* 62 */     if (dest == null) throw new IllegalArgumentException("dest cannot be null."); 
/*    */     try {
/* 64 */       System.arraycopy(src, srcPos, dest, destPos, length);
/* 65 */     } catch (ArrayIndexOutOfBoundsException ex) {
/* 66 */       throw new ArrayIndexOutOfBoundsException("Src: " + 
/* 67 */           Array.getLength(src) + ", " + srcPos + ", dest: " + 
/* 68 */           Array.getLength(dest) + ", " + destPos + ", count: " + length);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine3\\utils\SpineUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */