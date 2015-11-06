package com.lando.systems.mosfet.utils.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.IntIntMap;
import com.lando.systems.mosfet.screens.GamePlayScreen;
import com.lando.systems.mosfet.utils.Utils;

/**
 * Created by Doug on 10/8/2015.
 */
public class PerspectiveCameraController extends InputAdapter implements GestureDetector.GestureListener {
    private final PerspectiveCamera camera;
    private final IntIntMap keys = new IntIntMap();
    private int STRAFE_LEFT = Input.Keys.A;
    private int STRAFE_RIGHT = Input.Keys.D;
    private int FORWARD = Input.Keys.W;
    private int BACKWARD = Input.Keys.S;
    private int UP = Input.Keys.Q;
    private int DOWN = Input.Keys.E;
    private int ROTATELEFT = Input.Keys.J;
    private int ROTATERIGHT = Input.Keys.K;
    private float ROTATIONLOCKANGLE = 45;
    private float velocity = 5;
    private float panVelocity = 20;
    private float degreesPerPixel = 0.5f;
    private float zoomSpeed = 20;
    private float rotationSpeed = 90;
    private final Vector3 tmp = new Vector3();
    private float rotationAmount = 0;
    private float initialFOV;
    private float lastRotation;
    private Vector3 lookatPosition = new Vector3();
    private Vector2 levelSize;
    private Ray lookRay = new Ray();
    private final Plane zZeroPlane = new Plane(Vector3.Z, Vector3.Zero);
    public  boolean pause = false;
    private float currentFreeRotateAngle = 0;
    private GamePlayScreen level;

    public PerspectiveCameraController (PerspectiveCamera camera, GamePlayScreen level) {
        this.levelSize = new Vector2(level.level.getWidth(), level.level.getHeight());
        this.level = level;
        this.camera = camera;
    }

    @Override
    public boolean keyDown (int keycode) {
        keys.put(keycode, keycode);
        return true;
    }

    @Override
    public boolean keyUp (int keycode) {
        keys.remove(keycode, 0);
        return true;
    }

    /** Sets the velocity in units per second for moving forward, backward and strafing left/right.
     * @param velocity the velocity in units per second */
    public void setVelocity (float velocity) {
        this.velocity = velocity;
    }

    /** Sets how many degrees to rotate per pixel the mouse moved.
     * @param degreesPerPixel */
    public void setDegreesPerPixel (float degreesPerPixel) {
        this.degreesPerPixel = degreesPerPixel;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
//        float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
//        float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
//        camera.direction.rotate(camera.up, deltaX);
//        tmp.set(camera.direction).crs(camera.up).nor();
//        camera.direction.rotate(tmp, deltaY);
// camera.up.rotate(tmp, deltaY);
        return false;
    }

    public void update () {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update (float deltaTime) {
        if (pause) return;

//        if (keys.containsKey(FORWARD)) {
//            tmp.set(camera.direction.x, camera.direction.y, 0).nor().scl(deltaTime * velocity);
//            camera.position.add(tmp);
//        }
//        if (keys.containsKey(BACKWARD)) {
//            tmp.set(camera.direction.x, camera.direction.y, 0).nor().scl(-deltaTime * velocity);
//            camera.position.add(tmp);
//        }
//        if (keys.containsKey(STRAFE_LEFT)) {
//            tmp.set(camera.direction).crs(camera.up).nor().scl(-deltaTime * velocity);
//            camera.position.add(tmp);
//        }
//        if (keys.containsKey(STRAFE_RIGHT)) {
//            tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * velocity);
//            camera.position.add(tmp);
//        }
        if (keys.containsKey(UP)) {
            camera.fieldOfView = Math.min(camera.fieldOfView + (zoomSpeed * deltaTime), 90);
        }
        if (keys.containsKey(DOWN)) {
            camera.fieldOfView = Math.max(camera.fieldOfView - (zoomSpeed * deltaTime), 15);
        }
        if (Gdx.input.isKeyJustPressed(ROTATELEFT)) {
            rotationAmount += 90;
        }
        if (Gdx.input.isKeyJustPressed(ROTATERIGHT)) {
            rotationAmount -= 90;
        }

        lookRay.set(camera.position, camera.direction);
        Intersector.intersectRayPlane(lookRay, zZeroPlane, lookatPosition);

        float rotAbs = Math.abs(rotationAmount);
        if (rotAbs > 0){
            float rot = rotationSpeed * deltaTime * Math.signum(rotationAmount);
            if (Math.abs(rot) > rotAbs){
                rot = rotationAmount;
            }
            rotationAmount -= rot;
            camera.rotateAround(lookatPosition, Vector3.Z, rot);

        }

        if (lookatPosition.x < 0){
            camera.position.add( - lookatPosition.x, 0, 0);
        }
        if (lookatPosition.x > levelSize.x){
            camera.position.add(-(lookatPosition.x - levelSize.x), 0, 0);
        }
        if (lookatPosition.y < 0){
            camera.position.add(0, -lookatPosition.y, 0);
        }
        if (lookatPosition.y > levelSize.y){
            camera.position.add(0, -(lookatPosition.y - levelSize.y), 0);
        }

        camera.update(true);
    }

    public void rotateLeft(){
        float dr = (currentFreeRotateAngle) % ROTATIONLOCKANGLE;
        if (dr == 0) dr = ROTATIONLOCKANGLE;
        rotationAmount -= dr;
        currentFreeRotateAngle -= dr;
        currentFreeRotateAngle = Utils.wrapValue(currentFreeRotateAngle, 0, 360);
    }

    public void rotateRight(){
        float dr = ROTATIONLOCKANGLE - ((currentFreeRotateAngle) % ROTATIONLOCKANGLE);
        if (dr == 0) dr = ROTATIONLOCKANGLE;
        rotationAmount += dr;
        currentFreeRotateAngle += dr;
        currentFreeRotateAngle = Utils.wrapValue(currentFreeRotateAngle, 0, 360);
    }

    public void addRotation(float amount){
        rotationAmount += amount;
    }

    public boolean scrolled (int amount) {
        camera.fieldOfView = MathUtils.clamp(camera.fieldOfView + amount, 15, 90);
        return true;
    }


    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        initialFOV = camera.fieldOfView;
        lastRotation = Float.MIN_VALUE;
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        tmp.set(camera.direction.x, camera.direction.y, 0).nor().scl((deltaY/ Gdx.graphics.getWidth()) * panVelocity / (45/camera.fieldOfView));
        camera.position.add(tmp);

        tmp.set(camera.direction).crs(camera.up).nor().scl((-deltaX/ Gdx.graphics.getWidth()) * panVelocity / (45/camera.fieldOfView));
        camera.position.add(tmp);

        camera.update(true);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        float ratio = initialDistance / distance;
        camera.fieldOfView = MathUtils.clamp(initialFOV * ratio, 15, 90);
        camera.update();
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        if (pause){
            return false;
        }
        float initalAngle = (float)(Math.toDegrees(Math.atan2(initialPointer1.y - initialPointer2.y, initialPointer1.x - initialPointer2.x)));
        float newAngle = (float)(Math.toDegrees(Math.atan2(pointer1.y - pointer2.y, pointer1.x - pointer2.x)));
        float dif = newAngle - initalAngle;
        if (lastRotation == Float.MIN_VALUE){
            lastRotation = dif;
        }

        Intersector.intersectRayPlane(new Ray(camera.position, camera.direction), new Plane(Vector3.Z, Vector3.Zero), lookatPosition);
        currentFreeRotateAngle += dif - lastRotation;
        currentFreeRotateAngle = Utils.wrapValue(currentFreeRotateAngle, 0, 360);
        camera.rotateAround(lookatPosition, Vector3.Z, dif - lastRotation);
        camera.update();
        lastRotation = dif;
        return false;
    }
}
