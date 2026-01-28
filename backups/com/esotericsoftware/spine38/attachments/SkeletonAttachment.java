/*    */ package com.esotericsoftware.spine38.attachments;
/*    */ 
/*    */ import com.esotericsoftware.spine38.Skeleton;
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
/*    */ public class SkeletonAttachment
/*    */   extends Attachment
/*    */ {
/*    */   private Skeleton skeleton;
/*    */   
/*    */   public SkeletonAttachment(String name) {
/* 39 */     super(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public Skeleton getSkeleton() {
/* 44 */     return this.skeleton;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setSkeleton(Skeleton skeleton) {
/* 49 */     this.skeleton = skeleton;
/*    */   }
/*    */   
/*    */   public Attachment copy() {
/* 53 */     SkeletonAttachment copy = new SkeletonAttachment(this.name);
/* 54 */     copy.skeleton = this.skeleton;
/* 55 */     return copy;
/*    */   }
/*    */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\attachments\SkeletonAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */