/*     */ package com.esotericsoftware.spine38.utils;
/*     */ 
/*     */ import com.badlogic.gdx.assets.AssetDescriptor;
/*     */ import com.badlogic.gdx.assets.AssetLoaderParameters;
/*     */ import com.badlogic.gdx.assets.AssetManager;
/*     */ import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
/*     */ import com.badlogic.gdx.assets.loaders.FileHandleResolver;
/*     */ import com.badlogic.gdx.files.FileHandle;
/*     */ import com.badlogic.gdx.graphics.g2d.TextureAtlas;
/*     */ import com.badlogic.gdx.utils.Array;
/*     */ import com.esotericsoftware.spine38.SkeletonBinary;
/*     */ import com.esotericsoftware.spine38.SkeletonData;
/*     */ import com.esotericsoftware.spine38.SkeletonJson;
/*     */ import com.esotericsoftware.spine38.attachments.AtlasAttachmentLoader;
/*     */ import com.esotericsoftware.spine38.attachments.AttachmentLoader;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SkeletonDataLoader
/*     */   extends AsynchronousAssetLoader<SkeletonData, SkeletonDataLoader.SkeletonDataParameter>
/*     */ {
/*     */   private SkeletonData skeletonData;
/*     */   
/*     */   public SkeletonDataLoader(FileHandleResolver resolver) {
/*  67 */     super(resolver);
/*     */   }
/*     */   
/*     */   public void loadAsync(AssetManager manager, String fileName, FileHandle file, SkeletonDataParameter parameter) {
/*     */     AtlasAttachmentLoader atlasAttachmentLoader = null;
/*  72 */     float scale = 1.0F;
/*  73 */     AttachmentLoader attachmentLoader = null;
/*  74 */     if (parameter != null) {
/*  75 */       scale = parameter.scale;
/*  76 */       if (parameter.attachmentLoader != null) {
/*  77 */         attachmentLoader = parameter.attachmentLoader;
/*  78 */       } else if (parameter.atlasName != null) {
/*  79 */         atlasAttachmentLoader = new AtlasAttachmentLoader((TextureAtlas)manager.get(parameter.atlasName, TextureAtlas.class));
/*     */       } 
/*  81 */     }  if (atlasAttachmentLoader == null) {
/*  82 */       atlasAttachmentLoader = new AtlasAttachmentLoader((TextureAtlas)manager.get(file.pathWithoutExtension() + ".atlas", TextureAtlas.class));
/*     */     }
/*  84 */     if (file.extension().equalsIgnoreCase("skel")) {
/*  85 */       SkeletonBinary skeletonBinary = new SkeletonBinary((AttachmentLoader)atlasAttachmentLoader);
/*  86 */       skeletonBinary.setScale(scale);
/*  87 */       this.skeletonData = skeletonBinary.readSkeletonData(file);
/*     */     } else {
/*  89 */       SkeletonJson skeletonJson = new SkeletonJson((AttachmentLoader)atlasAttachmentLoader);
/*  90 */       skeletonJson.setScale(scale);
/*  91 */       this.skeletonData = skeletonJson.readSkeletonData(file);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public SkeletonData loadSync(AssetManager manager, String fileName, FileHandle file, SkeletonDataParameter parameter) {
/*  97 */     SkeletonData skeletonData = this.skeletonData;
/*  98 */     this.skeletonData = null;
/*  99 */     return skeletonData;
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, SkeletonDataParameter parameter) {
/* 104 */     if (parameter == null) return null; 
/* 105 */     if (parameter.attachmentLoader != null) return null; 
/* 106 */     Array<AssetDescriptor> dependencies = new Array();
/* 107 */     dependencies.add(new AssetDescriptor(parameter.atlasName, TextureAtlas.class));
/* 108 */     return dependencies;
/*     */   }
/*     */   
/*     */   public static class SkeletonDataParameter extends AssetLoaderParameters<SkeletonData> {
/*     */     public String atlasName;
/*     */     public AttachmentLoader attachmentLoader;
/* 114 */     public float scale = 1.0F;
/*     */ 
/*     */     
/*     */     public SkeletonDataParameter() {}
/*     */     
/*     */     public SkeletonDataParameter(String atlasName) {
/* 120 */       this.atlasName = atlasName;
/*     */     }
/*     */     
/*     */     public SkeletonDataParameter(String atlasName, float scale) {
/* 124 */       this.atlasName = atlasName;
/* 125 */       this.scale = scale;
/*     */     }
/*     */     
/*     */     public SkeletonDataParameter(AttachmentLoader attachmentLoader) {
/* 129 */       this.attachmentLoader = attachmentLoader;
/*     */     }
/*     */     
/*     */     public SkeletonDataParameter(AttachmentLoader attachmentLoader, float scale) {
/* 133 */       this.attachmentLoader = attachmentLoader;
/* 134 */       this.scale = scale;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine3\\utils\SkeletonDataLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */