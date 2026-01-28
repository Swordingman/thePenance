/*    */ package com.esotericsoftware.spine38.utils;
/*    */ 
/*    */ import com.badlogic.gdx.utils.Pool;
/*    */ import com.esotericsoftware.spine38.Skeleton;
/*    */ import com.esotericsoftware.spine38.SkeletonData;
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
/*    */ public class SkeletonPool
/*    */   extends Pool<Skeleton>
/*    */ {
/*    */   private SkeletonData skeletonData;
/*    */   
/*    */   public SkeletonPool(SkeletonData skeletonData) {
/* 40 */     this.skeletonData = skeletonData;
/*    */   }
/*    */   
/*    */   public SkeletonPool(SkeletonData skeletonData, int initialCapacity) {
/* 44 */     super(initialCapacity);
/* 45 */     this.skeletonData = skeletonData;
/*    */   }
/*    */   
/*    */   public SkeletonPool(SkeletonData skeletonData, int initialCapacity, int max) {
/* 49 */     super(initialCapacity, max);
/* 50 */     this.skeletonData = skeletonData;
/*    */   }
/*    */   
/*    */   protected Skeleton newObject() {
/* 54 */     return new Skeleton(this.skeletonData);
/*    */   }
/*    */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine3\\utils\SkeletonPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */