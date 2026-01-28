/*    */ package com.esotericsoftware.spine38.vertexeffects;
/*    */ 
/*    */ import com.badlogic.gdx.graphics.Color;
/*    */ import com.badlogic.gdx.math.Interpolation;
/*    */ import com.badlogic.gdx.math.Vector2;
/*    */ import com.esotericsoftware.spine38.Skeleton;
/*    */ import com.esotericsoftware.spine38.SkeletonMeshRenderer;
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
/*    */ public class SwirlEffect
/*    */   implements SkeletonMeshRenderer.VertexEffect
/*    */ {
/*    */   private float worldX;
/*    */   private float worldY;
/*    */   private float radius;
/*    */   private float angle;
/* 42 */   private Interpolation interpolation = (Interpolation)Interpolation.pow2Out;
/*    */   private float centerX;
/*    */   
/*    */   public SwirlEffect(float radius) {
/* 46 */     this.radius = radius;
/*    */   }
/*    */   private float centerY;
/*    */   public void begin(Skeleton skeleton) {
/* 50 */     this.worldX = skeleton.getX() + this.centerX;
/* 51 */     this.worldY = skeleton.getY() + this.centerY;
/*    */   }
/*    */   
/*    */   public void transform(Vector2 position, Vector2 uv, Color light, Color dark) {
/* 55 */     float x = position.x - this.worldX;
/* 56 */     float y = position.y - this.worldY;
/* 57 */     float dist = (float)Math.sqrt((x * x + y * y));
/* 58 */     if (dist < this.radius) {
/* 59 */       float theta = this.interpolation.apply(0.0F, this.angle, (this.radius - dist) / this.radius);
/* 60 */       float cos = SpineUtils.cos(theta), sin = SpineUtils.sin(theta);
/* 61 */       position.x = cos * x - sin * y + this.worldX;
/* 62 */       position.y = sin * x + cos * y + this.worldY;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void end() {}
/*    */   
/*    */   public void setRadius(float radius) {
/* 70 */     this.radius = radius;
/*    */   }
/*    */   
/*    */   public void setCenter(float centerX, float centerY) {
/* 74 */     this.centerX = centerX;
/* 75 */     this.centerY = centerY;
/*    */   }
/*    */   
/*    */   public void setCenterX(float centerX) {
/* 79 */     this.centerX = centerX;
/*    */   }
/*    */   
/*    */   public void setCenterY(float centerY) {
/* 83 */     this.centerY = centerY;
/*    */   }
/*    */   
/*    */   public void setAngle(float degrees) {
/* 87 */     this.angle = degrees * 0.017453292F;
/*    */   }
/*    */   
/*    */   public Interpolation getInterpolation() {
/* 91 */     return this.interpolation;
/*    */   }
/*    */   
/*    */   public void setInterpolation(Interpolation interpolation) {
/* 95 */     this.interpolation = interpolation;
/*    */   }
/*    */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\vertexeffects\SwirlEffect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */