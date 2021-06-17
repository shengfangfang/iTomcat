/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.catalina.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.sheff.util.ComponentUtil;
import org.apache.sheff.util.LogOutUtil;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/**
 * Base implementation of the {@link Lifecycle} interface that implements the
 * state transition rules for {@link Lifecycle#start()} and
 * 接口的基本实现，该接口实现了{@link Lifecycle＃start（）}和{@link Lifecycle#stop()}
 * 主要看这两个方法
 * Lifecycle 主要实现的一个  符合 Lifecycle 里面的图解逻辑
 * {@link Lifecycle#stop()}
 */
public abstract class LifecycleBase implements Lifecycle {

    private static final Log log = LogFactory.getLog(LifecycleBase.class);

    private static final StringManager sm = StringManager.getManager(LifecycleBase.class);

    public static int FIREEVENTCOUNT = 0;

    /**
     * The list of registered LifecycleListeners for event notifications.
     * 用于事件通知的已注册LifecycleListener的列表。
     */
    private final List<LifecycleListener> lifecycleListeners = new CopyOnWriteArrayList<>();
    private int lifecycleListenersCount = 0;


    /**
     * The current state of the source component.
     */
    private volatile LifecycleState state = LifecycleState.NEW;


    private boolean throwOnFailure = true;

    public String subClassType = "";

    public LifecycleBase() {
        subClassType = this.getClass().getName();
        LogOutUtil.log(this, "构造器完成,组件:" + ComponentUtil.getComponentName(subClassType) + "状态是：" + state.name());
    }

    /**
     * Will a {@link LifecycleException} thrown by a sub-class during
     * {@link #initInternal()}, {@link #startInternal()},
     * {@link #stopInternal()} or {@link #destroyInternal()} be re-thrown for
     * the caller to handle or will it be logged instead?
     * 子类在{@link #initInternal（）}，{@link #startInternal（）} {@link #stopInternal（）} 或
     * {@link #destroyInternal（）}期间抛出的{@link LifecycleException}是重新扔给调用者处理还是将其记录下来？
     *
     * @return {@code true} if the exception will be re-thrown, otherwise
     * {@code false}
     */
    public boolean getThrowOnFailure() {
        return throwOnFailure;
    }


    /**
     * Configure if a {@link LifecycleException} thrown by a sub-class during
     * {@link #initInternal()}, {@link #startInternal()},
     * {@link #stopInternal()} or {@link #destroyInternal()} will be re-thrown
     * for the caller to handle or if it will be logged instead.
     *
     * @param throwOnFailure {@code true} if the exception should be re-thrown,
     *                       otherwise {@code false}
     */
    public void setThrowOnFailure(boolean throwOnFailure) {
        this.throwOnFailure = throwOnFailure;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycleListeners.add(listener);
        lifecycleListenersCount++;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return lifecycleListeners.toArray(new LifecycleListener[0]);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycleListeners.remove(listener);
    }


    /**
     * Allow sub classes to fire {@link Lifecycle} events.
     * 允许子类触发{@link Lifecycle}事件。
     *
     * @param type Event type  事件的类型
     * @param data Data associated with event. 相关的数据
     */
    protected void fireLifecycleEvent(String type, Object data) {
        //属于构建一个事件 把自己对象穿到事件的对象里面去  再去触发事件
        LifecycleEvent event = new LifecycleEvent(this, type, data);
        for (LifecycleListener listener : lifecycleListeners) {
            listener.lifecycleEvent(event);
        }
        if( lifecycleListenersCount != 0){
            String listenerNames = "";
            int index = 1;
            for (LifecycleListener listener : lifecycleListeners) {
                listenerNames += index + "." + ComponentUtil.getComponentName(listener) + "; ";
                index++;
            }
            LogOutUtil.log(this, "监听器个数:" + lifecycleListenersCount + ";具体为[" + listenerNames + "]");
        }else{
            LogOutUtil.log(this, "无监听器");
        }

    }


    /**
     * 设计模式  模板方法
     *
     * @throws LifecycleException
     */
    @Override
    public final synchronized void init() throws LifecycleException {
        if (!state.equals(LifecycleState.NEW)) {
            invalidTransition(Lifecycle.BEFORE_INIT_EVENT);
        }
        try {
            //设置状态 INITIALIZING
            setStateInternal(LifecycleState.INITIALIZING, null, false);
            initInternal();
            //设置状态 INITIALIZING
            setStateInternal(LifecycleState.INITIALIZED, null, false);
            LogOutUtil.log(this, "设置组件状态state 和触发事件:" + LifecycleState.INITIALIZING + "|" + LifecycleState.INITIALIZED);
        } catch (Throwable t) {
            handleSubClassException(t, "lifecycleBase.initFail", toString());
        }

    }


    /**
     * Sub-classes implement this method to perform any instance initialisation
     * required.
     * 子类实现此方法以执行所需的任何实例初始化。
     *
     * @throws LifecycleException If the initialisation fails
     */
    protected abstract void initInternal() throws LifecycleException;


    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void start() throws LifecycleException {

        // STARTING_PREP  或者  STARTING  或者 STARTED  失效  抽象方法里面 Lifecycle 注释已经说过了
        if (LifecycleState.STARTING_PREP.equals(state) || LifecycleState.STARTING.equals(state) ||
                LifecycleState.STARTED.equals(state)) {

            if (log.isDebugEnabled()) {
                Exception e = new LifecycleException();
                log.debug(sm.getString("lifecycleBase.alreadyStarted", toString()), e);
            } else if (log.isInfoEnabled()) {
                log.info(sm.getString("lifecycleBase.alreadyStarted", toString()));
            }

            return;
        }

        //NEW 状态正常注解 init 初始化
        if (state.equals(LifecycleState.NEW)) {
            init();
        } else if (state.equals(LifecycleState.FAILED)) {
            //失败注状态 直接结束
            stop();
        } else if (!state.equals(LifecycleState.INITIALIZED) &&
                !state.equals(LifecycleState.STOPPED)) {
            //INITIALIZED 状态和 STOPPED 状态可以条用 start()  其余一律不允许调用
            invalidTransition(Lifecycle.BEFORE_START_EVENT);
        }

        try {
            //状态切换
            setStateInternal(LifecycleState.STARTING_PREP, null, false);
            //
            startInternal();
            if (state.equals(LifecycleState.FAILED)) {
                // This is a 'controlled' failure. The component put itself into the
                // FAILED state so call stop() to complete the clean-up.
                //这是“受控”故障。该组件将自身置于FAILED状态，因此请调用stop（）以完成清理。
                stop();
            } else if (!state.equals(LifecycleState.STARTING)) {
                // Shouldn't be necessary but acts as a check that sub-classes are
                // doing what they are supposed to. 没必要，但可以检查子类是否正在执行应有的功能。  子类是修改状态
                invalidTransition(Lifecycle.AFTER_START_EVENT);
            } else {
                //运行正常
                setStateInternal(LifecycleState.STARTED, null, false);
            }
        } catch (Throwable t) {
            // This is an 'uncontrolled' failure so put the component into the
            // FAILED state and throw an exception.
            handleSubClassException(t, "lifecycleBase.startFail", toString());
        }
    }


    /**
     * Sub-classes must ensure that the state is changed to
     * {@link LifecycleState#STARTING} during the execution of this method.
     * Changing state will trigger the {@link Lifecycle#START_EVENT} event.
     * <p>
     * If a component fails to start it may either throw a
     * {@link LifecycleException} which will cause it's parent to fail to start
     * or it can place itself in the error state in which case {@link #stop()}
     * will be called on the failed component but the parent component will
     * continue to start normally.
     * 类必须确保在执行此方法期间将状态更改为{@link LifecycleState＃STARTING}。
     * 更改状态将触发{@link Lifecycle＃START_EVENT}事件。
     * 如果组件无法启动，则可能抛出{@link LifecycleException}，这将导致其父项无法启动，
     * 或者它可能将自身置于错误状态，在这种情况下，将在组件上调用{@link #stop（）}。
     * 失败的组件，但父组件将继续正常启动。
     * <p>
     * 子类需要在运行中 设置父类的属性state 来标识子类运行中的状态是否是正常运行的
     *
     * @throws LifecycleException Start error occurred
     */
    protected abstract void startInternal() throws LifecycleException;


    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void stop() throws LifecycleException {
        // STOPPING_PREP  STOPPING STOPPED 无法调用 此方法
        if (LifecycleState.STOPPING_PREP.equals(state) || LifecycleState.STOPPING.equals(state) ||
                LifecycleState.STOPPED.equals(state)) {

            if (log.isDebugEnabled()) {
                Exception e = new LifecycleException();
                log.debug(sm.getString("lifecycleBase.alreadyStopped", toString()), e);
            } else if (log.isInfoEnabled()) {
                log.info(sm.getString("lifecycleBase.alreadyStopped", toString()));
            }

            return;
        }

        if (state.equals(LifecycleState.NEW)) {
            state = LifecycleState.STOPPED;
            return;
        }

        if (!state.equals(LifecycleState.STARTED) && !state.equals(LifecycleState.FAILED)) {
            invalidTransition(Lifecycle.BEFORE_STOP_EVENT);
        }

        try {
            if (state.equals(LifecycleState.FAILED)) {
                // Don't transition to STOPPING_PREP as that would briefly mark the
                // component as available but do ensure the BEFORE_STOP_EVENT is
                // fired
                fireLifecycleEvent(BEFORE_STOP_EVENT, null);
            } else {
                setStateInternal(LifecycleState.STOPPING_PREP, null, false);
            }
            //子类必须确保在执行此方法期间将状态更改为{@link LifecycleState＃STOPPING}。
            stopInternal();

            // Shouldn't be necessary but acts as a check that sub-classes are
            // doing what they are supposed to.
            if (!state.equals(LifecycleState.STOPPING) && !state.equals(LifecycleState.FAILED)) {
                invalidTransition(Lifecycle.AFTER_STOP_EVENT);
            }

            setStateInternal(LifecycleState.STOPPED, null, false);
        } catch (Throwable t) {
            handleSubClassException(t, "lifecycleBase.stopFail", toString());
        } finally {
            if (this instanceof Lifecycle.SingleUse) {
                // Complete stop process first
                setStateInternal(LifecycleState.STOPPED, null, false);
                destroy();
            }
        }
    }


    /**
     * Sub-classes must ensure that the state is changed to
     * {@link LifecycleState#STOPPING} during the execution of this method.
     * Changing state will trigger the {@link Lifecycle#STOP_EVENT} event.
     * 子类必须确保在执行此方法期间将状态更改为{@link LifecycleState＃STOPPING}。
     * 更改状态将触发{@link Lifecycle＃STOP_EVENT}事件。
     *
     * @throws LifecycleException Stop error occurred
     */
    protected abstract void stopInternal() throws LifecycleException;


    @Override
    public final synchronized void destroy() throws LifecycleException {
        if (LifecycleState.FAILED.equals(state)) {
            try {
                // Triggers clean-up
                stop();
            } catch (LifecycleException e) {
                // Just log. Still want to destroy.
                log.error(sm.getString("lifecycleBase.destroyStopFail", toString()), e);
            }
        }

        if (LifecycleState.DESTROYING.equals(state) || LifecycleState.DESTROYED.equals(state)) {
            if (log.isDebugEnabled()) {
                Exception e = new LifecycleException();
                log.debug(sm.getString("lifecycleBase.alreadyDestroyed", toString()), e);
            } else if (log.isInfoEnabled() && !(this instanceof Lifecycle.SingleUse)) {
                // Rather than have every component that might need to call
                // destroy() check for SingleUse, don't log an info message if
                // multiple calls are made to destroy()
                log.info(sm.getString("lifecycleBase.alreadyDestroyed", toString()));
            }

            return;
        }

        if (!state.equals(LifecycleState.STOPPED) && !state.equals(LifecycleState.FAILED) &&
                !state.equals(LifecycleState.NEW) && !state.equals(LifecycleState.INITIALIZED)) {
            invalidTransition(Lifecycle.BEFORE_DESTROY_EVENT);
        }

        try {
            setStateInternal(LifecycleState.DESTROYING, null, false);
            destroyInternal();
            setStateInternal(LifecycleState.DESTROYED, null, false);
        } catch (Throwable t) {
            handleSubClassException(t, "lifecycleBase.destroyFail", toString());
        }
    }


    /**
     * Sub-classes implement this method to perform any instance destruction
     * required.
     *
     * @throws LifecycleException If the destruction fails
     */
    protected abstract void destroyInternal() throws LifecycleException;


    /**
     * {@inheritDoc}
     */
    @Override
    public LifecycleState getState() {
        return state;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getStateName() {
        return getState().toString();
    }


    /**
     * Provides a mechanism for sub-classes to update the component state.
     * Calling this method will automatically fire any associated
     * {@link Lifecycle} event. It will also check that any attempted state
     * transition is valid for a sub-class.
     *
     * @param state The new state for this component
     * @throws LifecycleException when attempting to set an invalid state
     */
    protected synchronized void setState(LifecycleState state) throws LifecycleException {
        setStateInternal(state, null, true);
    }


    /**
     * Provides a mechanism for sub-classes to update the component state.
     * Calling this method will automatically fire any associated
     * {@link Lifecycle} event. It will also check that any attempted state
     * transition is valid for a sub-class.
     *
     * @param state The new state for this component
     * @param data  The data to pass to the associated {@link Lifecycle} event
     * @throws LifecycleException when attempting to set an invalid state
     */
    protected synchronized void setState(LifecycleState state, Object data)
            throws LifecycleException {
        setStateInternal(state, data, true);
    }


    private synchronized void setStateInternal(LifecycleState state, Object data, boolean check)
            throws LifecycleException {

        if (log.isDebugEnabled()) {
            log.debug(sm.getString("lifecycleBase.setState", this, state));
        }

        if (check) {
            // Must have been triggered by one of the abstract methods (assume
            // code in this class is correct)
            // null is never a valid state
            if (state == null) {
                invalidTransition("null");
                // Unreachable code - here to stop eclipse complaining about
                // a possible NPE further down the method
                return;
            }

            // Any method can transition to failed
            // startInternal() permits STARTING_PREP to STARTING
            // stopInternal() permits STOPPING_PREP to STOPPING and FAILED to
            // STOPPING
            if (!(state == LifecycleState.FAILED ||
                    (this.state == LifecycleState.STARTING_PREP &&
                            state == LifecycleState.STARTING) ||
                    (this.state == LifecycleState.STOPPING_PREP &&
                            state == LifecycleState.STOPPING) ||
                    (this.state == LifecycleState.FAILED &&
                            state == LifecycleState.STOPPING))) {
                // No other transition permitted
                invalidTransition(state.name());
            }
        }

        this.state = state;
        //log.warn(subClassType + "设置状态：" + this.state.name() + "，回调生命周期方法");
        String lifecycleEvent = state.getLifecycleEvent();
        if (lifecycleEvent != null) {
            fireLifecycleEvent(lifecycleEvent, data);
        }
    }


    private void invalidTransition(String type) throws LifecycleException {
        String msg = sm.getString("lifecycleBase.invalidTransition", type, toString(), state);
        throw new LifecycleException(msg);
    }


    private void handleSubClassException(Throwable t, String key, Object... args) throws LifecycleException {
        setStateInternal(LifecycleState.FAILED, null, false);
        ExceptionUtils.handleThrowable(t);
        String msg = sm.getString(key, args);
        if (getThrowOnFailure()) {
            if (!(t instanceof LifecycleException)) {
                t = new LifecycleException(msg, t);
            }
            throw (LifecycleException) t;
        } else {
            log.error(msg, t);
        }
    }
}
