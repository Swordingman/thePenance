/*    */ package com.esotericsoftware.spine38.vertexeffects;
/*    */ 
/*    */ import com.badlogic.gdx.graphics.Color;
/*    */ import com.badlogic.gdx.math.MathUtils;
/*    */ import com.badlogic.gdx.math.Vector2;
/*    */ import com.esotericsoftware.spine38.Skeleton;
/*    */ import com.esotericsoftware.spine38.SkeletonMeshRenderer;
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
/*    */ public class JitterEffect
/*    */   implements SkeletonMeshRenderer.VertexEffect
/*    */ {
/*    */   private float x;
/*    */   private float y;
/*    */   
/*    */   public JitterEffect(float x, float y) {
/* 42 */     this.x = x;
/* 43 */     this.y = y;
/*    */   }
/*    */ 
/*    */   
/*    */   public void begin(Skeleton skeleton) {}
/*    */   
/*    */   public void transform(Vector2 position, Vector2 uv, Color light, Color dark) {
/* 50 */     position.x += MathUtils.randomTriangular(-this.x, this.y);
/* 51 */     position.y += MathUtils.randomTriangular(-this.x, this.y);
/*    */   }
/*    */ 
/*    */   
/*    */   public void end() {}
/*    */   
/*    */   public void setJitter(float x, float y) {
/* 58 */     this.x = x;
/* 59 */     this.y = y;
/*    */   }
/*    */   
/*    */   public void setJitterX(float x) {
/* 63 */     this.x = x;
/*    */   }
/*    */   
/*    */   public void setJitterY(float y) {
/* 67 */     this.y = y;
/*    */   }
/*    */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\vertexeffects\JitterEffect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */