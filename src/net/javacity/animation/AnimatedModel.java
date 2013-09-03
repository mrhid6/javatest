package net.javacity.animation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

import net.javacity.lib.ResourceLocation;
import net.javacity.lib.managers.Animations;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;


public class AnimatedModel {
	public boolean animate = false;
	
	public Vector<Mesh> meshes;
	public int numMeshes;
	
	public IntBuffer idBuf;
	public FloatBuffer vBuf;
	public FloatBuffer tBuf;
	public FloatBuffer nBuf;
	public ByteBuffer iBuf;
	
	public int drawBufSize;
	
	public int vHandle = 0;
	public int tHandle = 0;
	public int iHandle = 0;
	public int nHandle = 0;
	Vector3f scale;
	public Vector3f min;
	public Vector3f max;
	public Vector3f size;
	public Vector3f center;
	//public Vector3f coffset;
	public Vector<Vert> verts;
	public Vector<Vert> orgVerts;
	public Vector<Tri> tris;
	public Vector<UV> uvs;
	public Vector<Normal> normals;
	public Vector<Joint> joints;
	public Matrix4f[] ajm;
	public Matrix4f[] rjm;
	public Matrix4f[] fjm;
	
	public float animationFPS; 
	public float currentTime; 
	public int totalFrames; 
	
	public float startTime = 0;
	public float endTime = 0;
	public float curTime = 0;
	int curFrame = 0;

	public int handle = -1;
	
	private Vector3f modelRot;
	private float modelYaw;
	private float timeToNextFrame = -1;
	
	private AnimationTime animationTime;

	private Texture texture;
	
	
	public AnimationTime getAnimationTime() {
		return animationTime;
	}
	
	public void setAnimationTime(AnimationTime time){
		this.animationTime = time;
	}
	
	public void setHandle(int handle) {
		this.handle = handle;
	}
	
	public AnimatedModel(String fileName, Vector3f scale, ResourceLocation texture){
		try {
			this.texture = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream(texture.getLocation()));
			
			modelRot = new Vector3f(0,0,0);
			modelYaw = 0.0f;
			this.scale = scale;
			orgVerts = new Vector<Vert>();
			verts = new Vector<Vert>();
			tris = new Vector<Tri>();
			uvs = new Vector<UV>();
			normals = new Vector<Normal>();
			joints = new Vector<Joint>();

			idBuf = BufferUtils.createIntBuffer(3);
			meshes = new Vector<Mesh>();
			URI logUri = new File(fileName).getAbsoluteFile().toURI();  
	          
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(  
		            new File(logUri)));
			
			byte[] btemp = new byte[10];
			in.read(btemp);
			
			String strtemp = new String(btemp);

			btemp = new byte[4];
			in.read(btemp);
			
			ByteBuffer byteBuf = BufferUtils.createByteBuffer(btemp.length);
			byteBuf.put(btemp);
			byteBuf.flip();
			//System.out.println("header: " + strtemp + " version: " + byteBuf.getInt());
			
			
			//num vertices
			btemp = new byte[2];
			in.read(btemp);

			byteBuf = BufferUtils.createByteBuffer(btemp.length);
			byteBuf.put(btemp);
			byteBuf.flip();
			
			int numVerts = byteBuf.asShortBuffer().get();
			//System.out.println("numVerts: " + numVerts);

			Vector3f temp = new Vector3f(0,0,0);
			 min = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);
			 max = new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY);
			 
			
			for(int i = 0; i < numVerts; i++){
				Vert tv = new Vert(i);
				
				btemp = new byte[1];
				in.read(btemp);
				//I don't need no flags.
				
				btemp = new byte[4];
				in.read(btemp);
				
				byteBuf = BufferUtils.createByteBuffer(btemp.length);
				byteBuf.put(btemp);
				byteBuf.flip();
				
				float tempX = byteBuf.asFloatBuffer().get() * scale.x;
				
				btemp = new byte[4];
				in.read(btemp);
				
				byteBuf = BufferUtils.createByteBuffer(btemp.length);
				byteBuf.put(btemp);
				byteBuf.flip();
				
				float tempY = byteBuf.asFloatBuffer().get() * scale.y;
				
				btemp = new byte[4];
				in.read(btemp);
				
				byteBuf = BufferUtils.createByteBuffer(btemp.length);
				byteBuf.put(btemp);
				byteBuf.flip();
				
				float tempZ = byteBuf.asFloatBuffer().get() * scale.z;
				btemp = new byte[1];
				in.read(btemp);
				
				byteBuf = BufferUtils.createByteBuffer(btemp.length);
				byteBuf.put(btemp);
				byteBuf.flip();
				int bid = (int) (byteBuf.get() & 0xFF);
				
				btemp = new byte[1];
				in.read(btemp);
				//I don't need no reference count.
				
				tv.pos = new Vector3f(tempX, tempY, tempZ);
				tv.boneID = bid;
				verts.add(tv);
				orgVerts.add(new Vert(tv));
				 temp = new Vector3f(tv.pos);
				 if(temp.x < min.x) min.x = temp.x;
				 if(temp.y < min.y) min.y = temp.y;
				 if(temp.z < min.z) min.z = temp.z;
				 
				 if(temp.x > max.x) max.x = temp.x;
				 if(temp.y > max.y) max.y = temp.y;
				 if(temp.z > max.z) max.z = temp.z;
			}
			
			 size = new Vector3f(max);
			 Vector3f.sub(size,  min, size);
			 Vector3f temp2 = new Vector3f(size);
			 temp2.scale(0.5f);
			 center = new Vector3f(temp2);
			 
			 //System.out.println("size: " + size.x + "\t" +size.y+ "\t"+size.z);
			
			btemp = new byte[2];
			in.read(btemp);
		
			byteBuf = BufferUtils.createByteBuffer(btemp.length);
			byteBuf.put(btemp);
			byteBuf.flip();
			
			int numTris = byteBuf.asShortBuffer().get();
			//System.out.println("numTris: " + numTris);
			
			for(int i = 0; i < numTris; i++){
					
				btemp = new byte[2];
				in.read(btemp);
				//don't need no flags
				
				int[] tva = new int[3];
				for(int j = 0; j < 3; j++){
					btemp = new byte[2];
					in.read(btemp);
					
					byteBuf = BufferUtils.createByteBuffer(btemp.length);
					byteBuf.put(btemp);
					byteBuf.flip();
					
					tva[j]  = byteBuf.asShortBuffer().get();
				}

				Normal[] tn = new Normal[3];
				for(int j = 0; j < 3; j++){
					btemp = new byte[4];
					in.read(btemp);
					
					byteBuf = BufferUtils.createByteBuffer(btemp.length);
					byteBuf.put(btemp);
					byteBuf.flip();
					
					float tempX = byteBuf.getFloat();
					
					btemp = new byte[4];
					in.read(btemp);
					
					byteBuf = BufferUtils.createByteBuffer(btemp.length);
					byteBuf.put(btemp);
					byteBuf.flip();
					
					float tempY = byteBuf.getFloat();
					
					btemp = new byte[4];
					in.read(btemp);
					
					byteBuf = BufferUtils.createByteBuffer(btemp.length);
					byteBuf.put(btemp);
					byteBuf.flip();
					
					float tempZ = byteBuf.getFloat();
					
					Normal n = new Normal(i + j);
					n.pos = new Vector3f(tempX, tempY, tempZ);
					normals.add(new Normal(n));
					tn[j] = new Normal(n);
				}
				
				float[] tsa = new float[3];
				for(int j = 0; j < 3; j++){
					btemp = new byte[4];
					in.read(btemp);
					
					byteBuf = BufferUtils.createByteBuffer(btemp.length);
					byteBuf.put(btemp);
					byteBuf.flip();
					
					tsa[j] = byteBuf.getFloat();
				}
				
				float[] tta = new float[3];
				for(int j = 0; j < 3; j++){
					btemp = new byte[4];
					in.read(btemp);
					
					byteBuf = BufferUtils.createByteBuffer(btemp.length);
					byteBuf.put(btemp);
					byteBuf.flip();
					
					tta[j] = byteBuf.getFloat();
				}
				
				UV[] tuv = new UV[3];
				for(int j = 0; j < 3; j++){
					tuv[j] = new UV(0, tsa[j], tta[j]);
				}
				
				btemp = new byte[2];
				in.read(btemp);
				
				tris.add(new Tri(i, tva,tuv, tn));
			}
			
			btemp = new byte[2];
			in.read(btemp);
			
			byteBuf = BufferUtils.createByteBuffer(btemp.length);
			byteBuf.put(btemp);
			byteBuf.flip();
			
			numMeshes = byteBuf.asShortBuffer().get();
			//System.out.println("numMeshes: " + numMeshes);
			
			for(int i = 0; i < numMeshes; i++){
				btemp = new byte[1];
				in.read(btemp);
				//don't need no flags
				
				btemp = new byte[32];
				in.read(btemp);
				
				String nametemp = new String(btemp);
				
				//System.out.println("nametemp: " + nametemp);
				
				btemp = new byte[2];
				in.read(btemp);
				
				byteBuf = BufferUtils.createByteBuffer(btemp.length);
				byteBuf.put(btemp);
				byteBuf.flip();
				
				int numMeshTris = byteBuf.asShortBuffer().get();
				//System.out.println("numMeshTris: " + numMeshTris);
				
				int[] triIndices = new int[numMeshTris];
				for(int j = 0; j < numMeshTris; j++){
					btemp = new byte[2];
					in.read(btemp);
					
					byteBuf = BufferUtils.createByteBuffer(btemp.length);
					byteBuf.put(btemp);
					byteBuf.flip();
					
					triIndices[j] = byteBuf.asShortBuffer().get();;
				}
				
				btemp = new byte[1];
				in.read(btemp);
				
				byteBuf = BufferUtils.createByteBuffer(btemp.length);
				byteBuf.put(btemp);
				byteBuf.flip();

				char c = new String(btemp).charAt(0);
				int matIndex = (int) Character.getNumericValue(c);
				//System.out.println("matIndex: " + matIndex);
				meshes.add(new Mesh(nametemp, numMeshTris, triIndices,matIndex));
				
			}
			
			
			btemp = new byte[2];
			in.read(btemp);
			
			byteBuf = BufferUtils.createByteBuffer(btemp.length);
			byteBuf.put(btemp);
			byteBuf.flip();
			
			int numMats= byteBuf.asShortBuffer().get();
			//System.out.println("numMats: " + numMats);
			
			btemp = new byte[numMats * 361];
			in.read(btemp);
			//skip the mats cause I don't need em.
			
			
			
			btemp = new byte[4];
			in.read(btemp);
			
			byteBuf = BufferUtils.createByteBuffer(btemp.length);
			byteBuf.put(btemp);
			byteBuf.flip();
			
			animationFPS = byteBuf.getFloat();
			
			btemp = new byte[4];
			in.read(btemp);
			
			byteBuf = BufferUtils.createByteBuffer(btemp.length);
			byteBuf.put(btemp);
			byteBuf.flip();
			
			currentTime = byteBuf.getFloat();
			
			btemp = new byte[4];
			in.read(btemp);
			
			byteBuf = BufferUtils.createByteBuffer(btemp.length);
			byteBuf.put(btemp);
			byteBuf.flip();
			
			totalFrames = byteBuf.getInt();
			
			btemp = new byte[2];
			in.read(btemp);
			
			byteBuf = BufferUtils.createByteBuffer(btemp.length);
			byteBuf.put(btemp);
			byteBuf.flip();
			
			int numJoints = byteBuf.asShortBuffer().get();
			
			//System.out.println("numJoints: " + numJoints);
			ajm = new Matrix4f[numJoints];
			rjm = new Matrix4f[numJoints];
			fjm = new Matrix4f[numJoints];
			
			for(int i = 0; i < numJoints; i++){
				Joint tempJoint = new Joint(i);
				ajm[i] = new Matrix4f();
				rjm[i] = new Matrix4f();
				fjm[i] = new Matrix4f();
				
				
				btemp = new byte[1];
				in.read(btemp);
				//don't need no flags
				
				btemp = new byte[32];
				in.read(btemp);
				
				tempJoint.name = new String(btemp);

				if(tempJoint.name.contains("base")){
					handle = i;
				}
				
				btemp = new byte[32];
				in.read(btemp);
				
				tempJoint.parentName = new String(btemp);

				for(int j = 0; j < joints.size(); j++){
					if(joints.get(j).name.compareTo(tempJoint.parentName) == 0){
						tempJoint.parentIndice = joints.get(j).indice;
						//System.out.println("name:" + tempJoint.name + "\tpname" + joints.get(j).indice + "\tpin: " + tempJoint.parentIndice + "\tj:" + j);
					}
				}

				btemp = new byte[4];
				in.read(btemp);
				
				byteBuf = BufferUtils.createByteBuffer(btemp.length);
				byteBuf.put(btemp);
				byteBuf.flip();
				
				float tempX = byteBuf.getFloat() * scale.x;
				
				btemp = new byte[4];
				in.read(btemp);
				
				byteBuf = BufferUtils.createByteBuffer(btemp.length);
				byteBuf.put(btemp);
				byteBuf.flip();
				
				float tempY = byteBuf.getFloat() * scale.y;
				
				btemp = new byte[4];
				in.read(btemp);
				
				byteBuf = BufferUtils.createByteBuffer(btemp.length);
				byteBuf.put(btemp);
				byteBuf.flip();
				
				float tempZ = byteBuf.getFloat() * scale.z;
				
			
				tempJoint.rot = new Vector3f(tempX, tempY, tempZ);
				
				
				btemp = new byte[4];
				in.read(btemp);
				
				byteBuf = BufferUtils.createByteBuffer(btemp.length);
				byteBuf.put(btemp);
				byteBuf.flip();
				
				tempX = byteBuf.getFloat() * scale.x;
				
				btemp = new byte[4];
				in.read(btemp);
				
				byteBuf = BufferUtils.createByteBuffer(btemp.length);
				byteBuf.put(btemp);
				byteBuf.flip();
				
				tempY = byteBuf.getFloat() * scale.y;
				
				btemp = new byte[4];
				in.read(btemp);
				
				byteBuf = BufferUtils.createByteBuffer(btemp.length);
				byteBuf.put(btemp);
				byteBuf.flip();
				
				tempZ = byteBuf.getFloat() * scale.z;

				tempJoint.pos = new Vector3f(tempX, tempY, tempZ);

				btemp = new byte[2];
				in.read(btemp);
				
				byteBuf = BufferUtils.createByteBuffer(btemp.length);
				byteBuf.put(btemp);
				byteBuf.flip();
				
				tempJoint.numKeyframesRot = byteBuf.asShortBuffer().get();
				
				btemp = new byte[2];
				in.read(btemp);
				
				byteBuf = BufferUtils.createByteBuffer(btemp.length);
				byteBuf.put(btemp);
				byteBuf.flip();
				
				tempJoint.numKeyframesTrans = byteBuf.asShortBuffer().get();
				
				tempJoint.KeyframeRot = new KeyframeRot[tempJoint.numKeyframesRot];
				
				for(int j = 0; j < tempJoint.numKeyframesRot; j++){	
					btemp = new byte[4];
					in.read(btemp);
					
					byteBuf = BufferUtils.createByteBuffer(btemp.length);
					byteBuf.put(btemp);
					byteBuf.flip();
					
					float tempTime = byteBuf.getFloat();
					
					btemp = new byte[4];
					in.read(btemp);
					
					byteBuf = BufferUtils.createByteBuffer(btemp.length);
					byteBuf.put(btemp);
					byteBuf.flip();
					
					tempX = byteBuf.getFloat() * scale.x;
					
					btemp = new byte[4];
					in.read(btemp);
					
					byteBuf = BufferUtils.createByteBuffer(btemp.length);
					byteBuf.put(btemp);
					byteBuf.flip();
					
					tempY = byteBuf.getFloat() * scale.y;
					
					btemp = new byte[4];
					in.read(btemp);
					
					byteBuf = BufferUtils.createByteBuffer(btemp.length);
					byteBuf.put(btemp);
					byteBuf.flip();
					
					tempZ = byteBuf.getFloat() * scale.z;
					
					Vector3f tempVec = new Vector3f(tempX, tempY, tempZ);
					
					tempJoint.KeyframeRot[j] = new KeyframeRot(tempTime, tempVec);
				}
				tempJoint.KeyframePos = new KeyframePos[tempJoint.numKeyframesTrans];
				
				
				for(int j = 0; j < tempJoint.numKeyframesRot; j++){
					btemp = new byte[4];
					in.read(btemp);
					
					byteBuf = BufferUtils.createByteBuffer(btemp.length);
					byteBuf.put(btemp);
					byteBuf.flip();
					
					float tempTime = byteBuf.getFloat();
					
					btemp = new byte[4];
					in.read(btemp);
					
					byteBuf = BufferUtils.createByteBuffer(btemp.length);
					byteBuf.put(btemp);
					byteBuf.flip();
					
					tempX = byteBuf.getFloat() * scale.x;
					
					btemp = new byte[4];
					in.read(btemp);
					
					byteBuf = BufferUtils.createByteBuffer(btemp.length);
					byteBuf.put(btemp);
					byteBuf.flip();
					
					tempY = byteBuf.getFloat() * scale.y;
					
					btemp = new byte[4];
					in.read(btemp);
					
					byteBuf = BufferUtils.createByteBuffer(btemp.length);
					byteBuf.put(btemp);
					byteBuf.flip();
					
					tempZ = byteBuf.getFloat() * scale.z;

					Vector3f tempVec = new Vector3f(tempX, tempY, tempZ);

					tempJoint.KeyframePos[j] = new KeyframePos(tempTime, tempVec);
					tempVec = null;
					
				}
				
				joints.add(tempJoint);
				
			}
			
			if(joints.size() > 0){
				startTime = joints.get(0).KeyframePos[0].time;
				endTime = joints.get(0).KeyframePos[joints.get(0).numKeyframesTrans - 1].time - startTime;
				
				timeToNextFrame = ((endTime - startTime)/totalFrames);
				
			}
			
			vBuf = BufferUtils.createFloatBuffer(tris.size() * (3 * 3));
			nBuf = BufferUtils.createFloatBuffer(tris.size() * (3 * 3));
			tBuf = BufferUtils.createFloatBuffer(tris.size() * (3 * 2));
			//coffset = new Vector3f(center.x, -center.y, center.z);
			setupJoints();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(startTime);
		animationTime = Animations.getAnimation("dwarf_start");
	}
	
	public float calcStartTime(float startframe){
		return timeToNextFrame*startframe;
	}
	
	public static void toFloatBuffer(Quaternion quat, FloatBuffer dest) {
		if (!dest.isDirect()) {
			System.out
					.println("QuaternionHelper toFloatBuffer was passed an indirect FloatBuffer!");
		} else if (dest.capacity() != 16) {
			System.out
					.println("QuaternionHelper toFloatBuffer was passed a buffer of the incorrect size!");
		} else {
			dest.clear();

			float x = quat.x;
			float y = quat.y;
			float z = quat.z;
			float w = quat.w;

			float x2 = x * x;
			float y2 = y * y;
			float z2 = z * z;
			float xy = x * y;
			float xz = x * z;
			float yz = y * z;
			float wx = w * x;
			float wy = w * y;
			float wz = w * z;

			dest.put(1.0f - 2.0f * (y2 + z2));
			dest.put(2.0f * (xy - wz));
			dest.put(2.0f * (xz + wy));
			dest.put(0.0f);
			dest.put(2.0f * (xy + wz));
			dest.put(1.0f - 2.0f * (x2 + z2));
			dest.put(2.0f * (yz - wx));
			dest.put(0.0f);
			dest.put(2.0f * (xz - wy));
			dest.put(2.0f * (yz + wx));
			dest.put(1.0f - 2.0f * (x2 + y2));
			dest.put(0.0f);
			dest.put(0.0f);
			dest.put(0.0f);
			dest.put(0.0f);
			dest.put(1.0f);

			dest.rewind();
		}
	}
	
	public void setupJoints(){
		for(int i = 0; i < joints.size(); i++){
			Joint tempJoint = joints.get(i);

			Vector3f rotationVector = new Vector3f();
            rotationVector.x =  (float) Math.toDegrees(tempJoint.rot.x);
            rotationVector.y =  (float) Math.toDegrees(tempJoint.rot.y);
            rotationVector.z =  (float) Math.toDegrees(tempJoint.rot.z);
            
            AngleMatrix(rotationVector, rjm[i]);
            rotationVector = null;

            rjm[i].m03 = tempJoint.pos.x;
            rjm[i].m13 = tempJoint.pos.y;
			rjm[i].m23 = tempJoint.pos.z;
	
			if(tempJoint.parentIndice != -1){
				 R_ConcatTransforms(ajm[tempJoint.parentIndice], rjm[i], ajm[i]);
				 fjm[i].load(ajm[i]);
			}
			
			else{
				ajm[i].load(rjm[i]);
				fjm[i].load(rjm[i]);
			}
		}
		
		for (int i = 0; i < verts.size(); i++ )
		{
			Vert vertex = verts.get(i);

			if (vertex.boneID != -1 && vertex.boneID != 255)
			{
				vertex.pos.x -= ajm[vertex.boneID].m03;
                vertex.pos.y -= ajm[vertex.boneID].m13;
                vertex.pos.z -= ajm[vertex.boneID].m23;
                Vector3f inverseRotationVector = new Vector3f();
                VectorIRotate(new Vector3f(vertex.pos.x, vertex.pos.y, vertex.pos.z), ajm[vertex.boneID], inverseRotationVector);
                vertex.pos.x = inverseRotationVector.x;
                vertex.pos.y = inverseRotationVector.y;
                vertex.pos.z = inverseRotationVector.z;
                inverseRotationVector = null;
			}

			verts.set(i,vertex);
			orgVerts.set(i,new Vert(vertex));
			vertex = null;
		}
	}
	
	public static final int unsignedShortToInt(byte[] b) 
	{
	    int i = 0;
	    i |= b[0] & 0xFF;
	    i <<= 8;
	    i |= b[1] & 0xFF;
	    return i;
	}
	
	public static void VectorIRotate(Vector3f in1, Matrix4f in2, Vector3f out) {
        out.x = in1.x * in2.m00 + in1.y * in2.m10 + in1.z * in2.m20;
        out.y = in1.x * in2.m01 + in1.y * in2.m11 + in1.z * in2.m21;
        out.z = in1.x * in2.m02 + in1.y * in2.m12 + in1.z * in2.m22;
    }
	
	public void init(){
		ARBVertexBufferObject.glGenBuffersARB(idBuf);
		vHandle = idBuf.get(0);
		tHandle = idBuf.get(1);
		nHandle = idBuf.get(2);

	}
	
	 public static void AngleMatrix(Vector3f angles, Matrix4f matrix) {
	        float angle;
	        float sr, sp, sy, cr, cp, cy;

	        float PI = 3.14f;
	        
	        angle = (float) (angles.z * (Math.PI * 2 / 360));
	        sy = (float) java.lang.Math.sin(angle);
	        cy = (float) java.lang.Math.cos(angle);
	        angle = (float) (angles.y * (Math.PI * 2 / 360));
	        sp = (float) java.lang.Math.sin(angle);
	        cp = (float) java.lang.Math.cos(angle);
	        angle = (float) (angles.x * (Math.PI * 2 / 360));
	        sr = (float) java.lang.Math.sin(angle);
	        cr = (float) java.lang.Math.cos(angle);

	        // matrix = (Z * Y) * X
	        matrix.m00 = cp * cy;
	        matrix.m10 = cp * sy;
	        matrix.m20 = -sp;
	        matrix.m01 = sr * sp * cy + cr * -sy;
	        matrix.m11 = sr * sp * sy + cr * cy;
	        matrix.m21 = sr * cp;
	        matrix.m02 = (cr * sp * cy + -sr * -sy);
	        matrix.m12 = (cr * sp * sy + -sr * cy);
	        matrix.m22 = cr * cp;
	    }
	 
	 public static void R_ConcatTransforms(Matrix4f in1, Matrix4f in2, Matrix4f out) {
	        out.m00 = in1.m00 * in2.m00 + in1.m01 * in2.m10 + in1.m02 * in2.m20;
	        out.m01 = in1.m00 * in2.m01 + in1.m01 * in2.m11 + in1.m02 * in2.m21;
	        out.m02 = in1.m00 * in2.m02 + in1.m01 * in2.m12 + in1.m02 * in2.m22;
	        out.m03 = in1.m00 * in2.m03 + in1.m01 * in2.m13 + in1.m02 * in2.m23 + in1.m03;
	        out.m10 = in1.m10 * in2.m00 + in1.m11 * in2.m10 + in1.m12 * in2.m20;
	        out.m11 = in1.m10 * in2.m01 + in1.m11 * in2.m11 + in1.m12 * in2.m21;
	        out.m12 = in1.m10 * in2.m02 + in1.m11 * in2.m12 + in1.m12 * in2.m22;
	        out.m13 = in1.m10 * in2.m03 + in1.m11 * in2.m13 + in1.m12 * in2.m23 + in1.m13;
	        out.m20 = in1.m20 * in2.m00 + in1.m21 * in2.m10 + in1.m22 * in2.m20;
	        out.m21 = in1.m20 * in2.m01 + in1.m21 * in2.m11 + in1.m22 * in2.m21;
	        out.m22 = in1.m20 * in2.m02 + in1.m21 * in2.m12 + in1.m22 * in2.m22;
	        out.m23 = in1.m20 * in2.m03 + in1.m21 * in2.m13 + in1.m22 * in2.m23 + in1.m23;
	    }
	 
	 public static void VectorTransform(Vector3f in1, Matrix4f in2, Vector3f out) {
        out.x = DotProduct(in1, new Vector3f(in2.m00, in2.m01, in2.m02)) + in2.m03;
        out.y = DotProduct(in1, new Vector3f(in2.m10, in2.m11, in2.m12)) + in2.m13;
        out.z = DotProduct(in1, new Vector3f(in2.m20, in2.m21, in2.m22)) + in2.m23;
    }
	 
	 public static float DotProduct(Vector3f v1, Vector3f v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }
	 
	 public static void VectorRotate(Vector3f in1, Matrix4f in2, Vector3f out) {
	        out.x = DotProduct(in1, new Vector3f(in2.m00, in2.m01, in2.m02));
	        out.y = DotProduct(in1, new Vector3f(in2.m10, in2.m11, in2.m12));
	        out.z = DotProduct(in1, new Vector3f(in2.m20, in2.m21, in2.m22));
	    }
	
	public Vector3f[] getJointInfo(float frame, int jointID, float myYaw){
		Vector3f[] jointInfo = new Vector3f[2];
		jointInfo[0] = new Vector3f(0,0,0);
		jointInfo[1] = new Vector3f(0,0,0);
		
		advanceAnimation(frame, true);
		
		Matrix4f worldMatrix = new Matrix4f();
		worldMatrix.load(fjm[jointID]);
		worldMatrix.invert();
        
		worldMatrix.rotate((float) Math.toRadians(myYaw), new Vector3f(0,1,0));	
		//VectorRotate(joints.get(jointID).pos, worldMatrix, jointInfo[0]);
		jointInfo[0] = new Vector3f(worldMatrix.m03,worldMatrix.m13,worldMatrix.m23);
        /*roll*/jointInfo[1].z = (float) Math.toDegrees(Math.atan2(worldMatrix.m10, worldMatrix.m00));
        /*pitch*/jointInfo[1].x = (float) Math.toDegrees(Math.asin(worldMatrix.m20));
        /*yaw*/jointInfo[1].y = (float) Math.toDegrees(Math.atan2(worldMatrix.m01, worldMatrix.m22));
		return jointInfo;
	}
	
	public Matrix4f getJointMatrix(float frame, int jointID, Vector3f myRot){
		advanceAnimation(frame, true);
		
		Matrix4f worldMatrix = new Matrix4f();
		worldMatrix.load(fjm[jointID]);
		Vector3f orgPos = new Vector3f(worldMatrix.m03, worldMatrix.m13, worldMatrix.m23);
		//worldMatrix.invert();
		worldMatrix.m03 = orgPos.x; 
		worldMatrix.m13 = orgPos.y; 
		worldMatrix.m23 = orgPos.z; 
		
		Matrix4f tempRotMat = new Matrix4f();
		AngleMatrix(myRot, tempRotMat);
		Matrix4f out = new Matrix4f();
		Matrix4f.mul(worldMatrix, tempRotMat, out);
		
		//worldMatrix.rotate((float) Math.toRadians(-myRot.x), new Vector3f(0,1,0));
		//worldMatrix.rotate((float) Math.toRadians(-myRot.y), new Vector3f(1,0,0));
		return out;
	}
			
	 
	public void renderSetUp(float delta, float frame){
		vBuf.clear();
		tBuf.clear();
		nBuf.clear();
		
        advanceAnimation(frame, true);

		for(int i = 0; i < verts.size(); i++){
			Vert v1 = new Vert(orgVerts.get(i));
			Matrix4f worldMatrix = new Matrix4f();
	        worldMatrix.setIdentity();
	        
			if(v1.boneID != -1 && v1.boneID != 255){
				worldMatrix.load(fjm[v1.boneID]);
			}

			
			 Vector3f animationVector = new Vector3f();
             VectorRotate(new Vector3f(v1.pos.x, v1.pos.y, v1.pos.z), worldMatrix, animationVector);
             animationVector.x += worldMatrix.m03;
             animationVector.y += worldMatrix.m13;
             animationVector.z += worldMatrix.m23;
             Vector3f animatedPos = new Vector3f(animationVector);
		     verts.get(i).pos.set(animatedPos);
		     animatedPos = null;
		     worldMatrix = null;
		     v1 = null;
		}
		
		for(int j = 0; j < numMeshes; j++){
			for(int i = 0; i < meshes.get(j).numTris; i++){
				Tri q = tris.get(meshes.get(j).triIndices[i]);
				for(int h = 0; h < q.vert.length; h++){
					Vert v1 = verts.get(q.vert[h]);
					
			        vBuf.put(v1.pos.x).put(v1.pos.y).put(v1.pos.z);
			        v1 = null;
				}
				
				for(int h = 0; h < q.uvs.length; h++){
					UV uv = q.uvs[h];
					
					tBuf.put(uv.u).put(uv.v);
					uv = null;
				}
				
				nBuf.put(-q.n1.pos.x).put(-q.n1.pos.y).put(-q.n1.pos.z);
				nBuf.put(-q.n2.pos.x).put(-q.n2.pos.y).put(-q.n2.pos.z);
				nBuf.put(-q.n3.pos.x).put(-q.n3.pos.y).put(-q.n3.pos.z);
				q = null;
			} 
		}	
		vBuf.flip();
		tBuf.flip();
		nBuf.flip();
	}
	
	public void render(){
		texture.bind();
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);	
		GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
		
		renderSetUp(0, animationTime.getCurrentTime());
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, nHandle);
		ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, nBuf, ARBVertexBufferObject.GL_DYNAMIC_DRAW_ARB);
		GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0);

		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, tHandle);
		ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, tBuf, ARBVertexBufferObject.GL_DYNAMIC_DRAW_ARB);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);

		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vHandle);
		ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vBuf, ARBVertexBufferObject.GL_DYNAMIC_DRAW_ARB);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);

		
		
		GL11.glPushMatrix();
		//GL11.glRotatef(modelRot.x, 0, 1, 0);
		//GL11.glRotatef(modelRot.y, 1, 0, 0);

		Matrix4f mat = new Matrix4f();

		mat.setIdentity();
		
		FloatBuffer fb1 = BufferUtils.createFloatBuffer(4 * 4);

		AngleMatrix(modelRot, mat);
		mat.storeTranspose(fb1);
		fb1.flip();
		//fb.
		GL11.glMultMatrix(fb1);

			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, tris.size() * 3 );
		GL11.glPopMatrix();
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);	
		GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
		
	}
	
	public void animate(){
		if(animationTime.shouldAnimate()){
			animationTime.loopAnimation();
		}
		
	}
	
	public void advanceAnimation(float time, boolean aim){
		for(int i = 0; i < joints.size(); i++){
			Vector3f transVec = new Vector3f(0,0,0);
			Matrix4f mat_transform = new Matrix4f();
			int frame = 0;
			Joint pJoint = joints.get(i);

			fjm[pJoint.indice].load(ajm[pJoint.indice]);
			
			if ( pJoint.numKeyframesRot!= 0 && pJoint.numKeyframesTrans != 0 ){
				
				while ( frame < pJoint.numKeyframesTrans && pJoint.KeyframePos[frame].time < time ){
					frame++;
				}
				
				if ( frame == 0 ) transVec = new Vector3f(pJoint.KeyframePos[0].pos);
				else if ( frame >= pJoint.numKeyframesTrans) transVec = new Vector3f(pJoint.KeyframePos[pJoint.numKeyframesTrans-1].pos);
				else{
					KeyframePos curFrame  = pJoint.KeyframePos[frame];
					KeyframePos prevFrame = pJoint.KeyframePos[frame-1];
	
				
					float timeDelta = curFrame.time-prevFrame.time;
					float interpValue = ( float )(( ( float )time-( float )prevFrame.time )/( float )timeDelta );
	
					transVec.x = prevFrame.pos.x+( curFrame.pos.x-prevFrame.pos.x )*interpValue;
					transVec.y = prevFrame.pos.y+( curFrame.pos.y-prevFrame.pos.y )*interpValue;
					transVec.z = prevFrame.pos.z+( curFrame.pos.z-prevFrame.pos.z )*interpValue;
					curFrame = null;
					prevFrame = null;
				}
				
				while ( frame < pJoint.numKeyframesRot
					&& pJoint.KeyframeRot[frame].time < time )
				{
					frame++;
				}
				
				if ( frame == 0 ){
					Vector3f rotationVector = new Vector3f();
		            rotationVector.x = (float) Math.toDegrees(pJoint.KeyframeRot[0].rot.x);
		            rotationVector.y = (float) Math.toDegrees(pJoint.KeyframeRot[0].rot.y);
		            rotationVector.z = (float) Math.toDegrees(pJoint.KeyframeRot[0].rot.z);
		            AngleMatrix(rotationVector, mat_transform);
		            rotationVector = null;
				}
				else if ( frame >= pJoint.numKeyframesRot ){
					Vector3f rotationVector = new Vector3f();
					rotationVector.x = (float) Math.toDegrees(pJoint.KeyframeRot[pJoint.numKeyframesRot -1].rot.x);
		            rotationVector.y = (float) Math.toDegrees(pJoint.KeyframeRot[pJoint.numKeyframesRot -1].rot.y);
		            rotationVector.z = (float) Math.toDegrees(pJoint.KeyframeRot[pJoint.numKeyframesRot -1].rot.z);
		            AngleMatrix(rotationVector, mat_transform);
		            rotationVector = null;
				}
				else
				{
	
					KeyframeRot curFrame = pJoint.KeyframeRot[frame];
					KeyframeRot prevFrame = pJoint.KeyframeRot[frame-1];
	
					float timeDelta = curFrame.time-prevFrame.time;
					float interpValue = ( float )(( time-prevFrame.time )/timeDelta );
	
					Vector3f rotVec = new Vector3f(0,0,0);
	
					rotVec.x = (float) (prevFrame.rot.x+(curFrame.rot.x-prevFrame.rot.x )*interpValue);
					rotVec.y = (float) (prevFrame.rot.y+(curFrame.rot.y-prevFrame.rot.y )*interpValue);
					rotVec.z = (float) (prevFrame.rot.z+(curFrame.rot.z-prevFrame.rot.z )*interpValue);
	
					
					
					Vector3f rotationVector = new Vector3f();
		            rotationVector.x = (float) Math.toDegrees(rotVec.x);
		            rotationVector.y = (float) Math.toDegrees(rotVec.y);
		            rotationVector.z = (float) Math.toDegrees(rotVec.z);
		            AngleMatrix(rotationVector, mat_transform);
		            
		            curFrame = null;
		            prevFrame = null;
		            rotVec = null;
		            rotationVector = null;
				}
				
				
				mat_transform.m03 = transVec.x;
				mat_transform.m13 = transVec.y;
				mat_transform.m23 = transVec.z;
	
				Matrix4f relativeFinal = new Matrix4f();
				R_ConcatTransforms(rjm[pJoint.indice], mat_transform,relativeFinal);
	
				if ( pJoint.parentIndice == -1 ){
					fjm[pJoint.indice].load(relativeFinal);
				}
				
				else
				{
					R_ConcatTransforms(fjm[pJoint.parentIndice],relativeFinal,fjm[pJoint.indice]);
				}
				relativeFinal = null;
			}
			
			pJoint = null;
			mat_transform = null;
			transVec = null;
		}
	}
}
