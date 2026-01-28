/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.utils.Array;
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
/*     */ public class TransformConstraintData
/*     */   extends ConstraintData
/*     */ {
/*  38 */   final Array<BoneData> bones = new Array(); BoneData target; float rotateMix;
/*     */   float translateMix;
/*     */   float scaleMix;
/*     */   float shearMix;
/*     */   float offsetRotation;
/*     */   
/*     */   public TransformConstraintData(String name) {
/*  45 */     super(name);
/*     */   }
/*     */   float offsetX; float offsetY; float offsetScaleX; float offsetScaleY; float offsetShearY; boolean relative; boolean local;
/*     */   
/*     */   public Array<BoneData> getBones() {
/*  50 */     return this.bones;
/*     */   }
/*     */ 
/*     */   
/*     */   public BoneData getTarget() {
/*  55 */     return this.target;
/*     */   }
/*     */   
/*     */   public void setTarget(BoneData target) {
/*  59 */     if (target == null) throw new IllegalArgumentException("target cannot be null."); 
/*  60 */     this.target = target;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getRotateMix() {
/*  65 */     return this.rotateMix;
/*     */   }
/*     */   
/*     */   public void setRotateMix(float rotateMix) {
/*  69 */     this.rotateMix = rotateMix;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getTranslateMix() {
/*  74 */     return this.translateMix;
/*     */   }
/*     */   
/*     */   public void setTranslateMix(float translateMix) {
/*  78 */     this.translateMix = translateMix;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getScaleMix() {
/*  83 */     return this.scaleMix;
/*     */   }
/*     */   
/*     */   public void setScaleMix(float scaleMix) {
/*  87 */     this.scaleMix = scaleMix;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getShearMix() {
/*  92 */     return this.shearMix;
/*     */   }
/*     */   
/*     */   public void setShearMix(float shearMix) {
/*  96 */     this.shearMix = shearMix;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getOffsetRotation() {
/* 101 */     return this.offsetRotation;
/*     */   }
/*     */   
/*     */   public void setOffsetRotation(float offsetRotation) {
/* 105 */     this.offsetRotation = offsetRotation;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getOffsetX() {
/* 110 */     return this.offsetX;
/*     */   }
/*     */   
/*     */   public void setOffsetX(float offsetX) {
/* 114 */     this.offsetX = offsetX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getOffsetY() {
/* 119 */     return this.offsetY;
/*     */   }
/*     */   
/*     */   public void setOffsetY(float offsetY) {
/* 123 */     this.offsetY = offsetY;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getOffsetScaleX() {
/* 128 */     return this.offsetScaleX;
/*     */   }
/*     */   
/*     */   public void setOffsetScaleX(float offsetScaleX) {
/* 132 */     this.offsetScaleX = offsetScaleX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getOffsetScaleY() {
/* 137 */     return this.offsetScaleY;
/*     */   }
/*     */   
/*     */   public void setOffsetScaleY(float offsetScaleY) {
/* 141 */     this.offsetScaleY = offsetScaleY;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getOffsetShearY() {
/* 146 */     return this.offsetShearY;
/*     */   }
/*     */   
/*     */   public void setOffsetShearY(float offsetShearY) {
/* 150 */     this.offsetShearY = offsetShearY;
/*     */   }
/*     */   
/*     */   public boolean getRelative() {
/* 154 */     return this.relative;
/*     */   }
/*     */   
/*     */   public void setRelative(boolean relative) {
/* 158 */     this.relative = relative;
/*     */   }
/*     */   
/*     */   public boolean getLocal() {
/* 162 */     return this.local;
/*     */   }
/*     */   
/*     */   public void setLocal(boolean local) {
/* 166 */     this.local = local;
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\TransformConstraintData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */