/*    */ package com.esotericsoftware.spine38.attachments;
/*    */ 
/*    */ import com.badlogic.gdx.graphics.Color;
/*    */ import com.esotericsoftware.spine38.utils.SpineUtils;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PathAttachment
/*    */   extends VertexAttachment
/*    */ {
/*    */   float[] lengths;
/*    */   boolean closed;
/*    */   boolean constantSpeed;
/* 46 */   final Color color = new Color(1.0F, 0.5F, 0.0F, 1.0F);
/*    */   
/*    */   public PathAttachment(String name) {
/* 49 */     super(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean getClosed() {
/* 54 */     return this.closed;
/*    */   }
/*    */   
/*    */   public void setClosed(boolean closed) {
/* 58 */     this.closed = closed;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean getConstantSpeed() {
/* 64 */     return this.constantSpeed;
/*    */   }
/*    */   
/*    */   public void setConstantSpeed(boolean constantSpeed) {
/* 68 */     this.constantSpeed = constantSpeed;
/*    */   }
/*    */ 
/*    */   
/*    */   public float[] getLengths() {
/* 73 */     return this.lengths;
/*    */   }
/*    */   
/*    */   public void setLengths(float[] lengths) {
/* 77 */     this.lengths = lengths;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Color getColor() {
/* 83 */     return this.color;
/*    */   }
/*    */   
/*    */   public Attachment copy() {
/* 87 */     PathAttachment copy = new PathAttachment(this.name);
/* 88 */     copyTo(copy);
/* 89 */     copy.lengths = new float[this.lengths.length];
/* 90 */     SpineUtils.arraycopy(this.lengths, 0, copy.lengths, 0, this.lengths.length);
/* 91 */     copy.closed = this.closed;
/* 92 */     copy.constantSpeed = this.constantSpeed;
/* 93 */     copy.color.set(this.color);
/* 94 */     return copy;
/*    */   }
/*    */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\attachments\PathAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */