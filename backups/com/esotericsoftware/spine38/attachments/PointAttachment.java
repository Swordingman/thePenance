/*     */ package com.esotericsoftware.spine38.attachments;
/*     */ 
/*     */ import com.badlogic.gdx.graphics.Color;
/*     */ import com.badlogic.gdx.math.MathUtils;
/*     */ import com.badlogic.gdx.math.Vector2;
/*     */ import com.esotericsoftware.spine38.Bone;
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
/*     */ public class PointAttachment
/*     */   extends Attachment
/*     */ {
/*     */   float x;
/*     */   float y;
/*     */   float rotation;
/*  49 */   final Color color = new Color(0.9451F, 0.9451F, 0.0F, 1.0F);
/*     */   
/*     */   public PointAttachment(String name) {
/*  52 */     super(name);
/*     */   }
/*     */   
/*     */   public float getX() {
/*  56 */     return this.x;
/*     */   }
/*     */   
/*     */   public void setX(float x) {
/*  60 */     this.x = x;
/*     */   }
/*     */   
/*     */   public float getY() {
/*  64 */     return this.y;
/*     */   }
/*     */   
/*     */   public void setY(float y) {
/*  68 */     this.y = y;
/*     */   }
/*     */   
/*     */   public float getRotation() {
/*  72 */     return this.rotation;
/*     */   }
/*     */   
/*     */   public void setRotation(float rotation) {
/*  76 */     this.rotation = rotation;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Color getColor() {
/*  82 */     return this.color;
/*     */   }
/*     */   
/*     */   public Vector2 computeWorldPosition(Bone bone, Vector2 point) {
/*  86 */     point.x = this.x * bone.getA() + this.y * bone.getB() + bone.getWorldX();
/*  87 */     point.y = this.x * bone.getC() + this.y * bone.getD() + bone.getWorldY();
/*  88 */     return point;
/*     */   }
/*     */   
/*     */   public float computeWorldRotation(Bone bone) {
/*  92 */     float cos = MathUtils.cosDeg(this.rotation), sin = MathUtils.sinDeg(this.rotation);
/*  93 */     float x = cos * bone.getA() + sin * bone.getB();
/*  94 */     float y = cos * bone.getC() + sin * bone.getD();
/*  95 */     return (float)Math.atan2(y, x) * 57.295776F;
/*     */   }
/*     */   
/*     */   public Attachment copy() {
/*  99 */     PointAttachment copy = new PointAttachment(this.name);
/* 100 */     copy.x = this.x;
/* 101 */     copy.y = this.y;
/* 102 */     copy.rotation = this.rotation;
/* 103 */     copy.color.set(this.color);
/* 104 */     return copy;
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\attachments\PointAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */