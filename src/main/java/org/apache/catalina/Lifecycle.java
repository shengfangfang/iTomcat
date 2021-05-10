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
package org.apache.catalina;


/**
 * Common interface for component life cycle methods.  Catalina components
 * may implement this interface (as well as the appropriate interface(s) for
 * the functionality they support) in order to provide a consistent mechanism
 * to start and stop the component.
 * 组件生命周期方法的通用接口。 Catalina组件可以实现此接口
 * （以及它们支持的功能的相应接口），以便提供一致的机制来启动和停止该组件。
 * <br>
 * The valid state transitions for components that support {@link Lifecycle}
 * 支持的组件的有效状态转换
 * are:
 * <pre>
 *            start()
 *  -----------------------------
 *  |                           |
 *  | init()                    |
 * NEW -»-- INITIALIZING        |
 * | |           |              |     ------------------«-----------------------
 * | |           |auto          |     |                                        |
 * | |          \|/    start() \|/   \|/     auto          auto         stop() |
 * | |      INITIALIZED --»-- STARTING_PREP --»- STARTING --»- STARTED --»---  |
 * | |         |                                                            |  |
 * | |destroy()|                                                            |  |
 * | --»-----«--    ------------------------«--------------------------------  ^
 * |     |          |                                                          |
 * |     |         \|/          auto                 auto              start() |
 * |     |     STOPPING_PREP ----»---- STOPPING ------»----- STOPPED -----»-----
 * |    \|/                               ^                     |  ^
 * |     |               stop()           |                     |  |
 * |     |       --------------------------                     |  |
 * |     |       |                                              |  |
 * |     |       |    destroy()                       destroy() |  |
 * |     |    FAILED ----»------ DESTROYING ---«-----------------  |
 * |     |                        ^     |                          |
 * |     |     destroy()          |     |auto                      |
 * |     --------»-----------------    \|/                         |
 * |                                 DESTROYED                     |
 * |                                                               |
 * |                            stop()                             |
 * ----»-----------------------------»------------------------------
 *
 * Any state can transition to FAILED.
 * 任何状态都可以转换为FAILED。
 *
 * Calling start() while a component is in states STARTING_PREP, STARTING or
 * STARTED has no effect.
 * 当组件处于状态STARTING_PREP，STARTING或STARTED时调用start（）无效。
 *
 * Calling start() while a component is in state NEW will cause init() to be
 * called immediately after the start() method is entered.
 * 组件处于NEW状态时调用start（）将导致在输入start（）方法后立即调用init（）。
 *
 * Calling stop() while a component is in states STOPPING_PREP, STOPPING or
 * STOPPED has no effect.
 * 当组件处于STOPPING_PREP，STOPPING或STOPPED状态时，调用stop（）无效。
 *
 * Calling stop() while a component is in state NEW transitions the component
 * to STOPPED. This is typically encountered when a component fails to start and
 * does not start all its sub-components. When the component is stopped, it will
 * try to stop all sub-components - even those it didn't start.
 * 当组件处于状态NEW时调用stop（）会将组件转换为STOPPED。
 * 当组件无法启动并且没有启动其所有子组件时，通常会遇到这种情况。
 * 当组件停止时，它将尝试停止所有子组件-包括那些尚未启动的子组件。
 *
 * Attempting any other transition will throw {@link LifecycleException}. 尝试任何其他过渡都会抛出
 *
 * </pre>
 * The {@link LifecycleEvent}s fired during state changes are defined in the
 * methods that trigger the changed. No {@link LifecycleEvent}s are fired if the
 * attempted transition is not valid.
 *
 * @author Craig R. McClanahan
 */
public interface Lifecycle {


    //常量清单  生命周期事件的状态
    // ----------------------------------------------------- Manifest Constants


    /**
     * The LifecycleEvent type for the "component before init" event.
     */
    String BEFORE_INIT_EVENT = "before_init";


    /**
     * The LifecycleEvent type for the "component after init" event.
     */
    String AFTER_INIT_EVENT = "after_init";


    /**
     * The LifecycleEvent type for the "component start" event.
     */
    String START_EVENT = "start";


    /**
     * The LifecycleEvent type for the "component before start" event.
     */
    String BEFORE_START_EVENT = "before_start";


    /**
     * The LifecycleEvent type for the "component after start" event.
     */
    String AFTER_START_EVENT = "after_start";


    /**
     * The LifecycleEvent type for the "component stop" event.
     */
    String STOP_EVENT = "stop";


    /**
     * The LifecycleEvent type for the "component before stop" event.
     */
    String BEFORE_STOP_EVENT = "before_stop";


    /**
     * The LifecycleEvent type for the "component after stop" event.
     */
    String AFTER_STOP_EVENT = "after_stop";


    /**
     * The LifecycleEvent type for the "component after destroy" event.
     */
    String AFTER_DESTROY_EVENT = "after_destroy";


    /**
     * The LifecycleEvent type for the "component before destroy" event.
     */
    String BEFORE_DESTROY_EVENT = "before_destroy";


    /**
     * The LifecycleEvent type for the "periodic" event.
     */
     String PERIODIC_EVENT = "periodic";


    /**
     * The LifecycleEvent type for the "configure_start" event. Used by those
     * components that use a separate component to perform configuration and
     * need to signal when configuration should be performed - usually after
     * {@link #BEFORE_START_EVENT} and before {@link #START_EVENT}.
     */
    String CONFIGURE_START_EVENT = "configure_start";


    /**
     * The LifecycleEvent type for the "configure_stop" event. Used by those
     * components that use a separate component to perform configuration and
     * need to signal when de-configuration should be performed - usually after
     * {@link #STOP_EVENT} and before {@link #AFTER_STOP_EVENT}.
     */
    String CONFIGURE_STOP_EVENT = "configure_stop";


    // --------------------------------------------------------- Public Methods


    /**
     * Add a LifecycleEvent listener to this component.
     *
     * @param listener The listener to add
     */
    void addLifecycleListener(LifecycleListener listener);


    /**
     * Get the life cycle listeners associated with this life cycle.
     * 获取与此生命周期相关联的生命周期侦听器
     * @return An array containing the life cycle listeners associated with this
     * life cycle. If this component has no listeners registered, a
     * zero-length array is returned.
     */
    LifecycleListener[] findLifecycleListeners();


    /**
     * Remove a LifecycleEvent listener from this component.
     *
     * @param listener The listener to remove
     */
    void removeLifecycleListener(LifecycleListener listener);


    /**
     * 准备要启动的组件。此方法应执行对象创建后所需的任何初始化。 将按以下顺序触发：
     * Prepare the component for starting. This method should perform any
     * initialization required post object creation. The following
     * {@link LifecycleEvent}s will be fired in the following order:
     * <ol>
     *   <li>INIT_EVENT: On the successful completion of component
     *                   initialization.</li>
     * </ol>
     *
     * @throws LifecycleException if this component detects a fatal error
     *                            that prevents this component from being used
     */
    void init() throws LifecycleException;

    /**
     * Prepare for the beginning of active use of the    methods other than
     * property getters/setters and life cycle methods of this component. This
     * method should be called before any of the    methods other than
     * property getters/setters and life cycle methods of this component are
     * utilized. The following {@link LifecycleEvent}s will be fired in the
     * following order:
     * 准备开始积极使用该组件的属性getters/setters 和生命周期方法以外的方法。在利用此组件的属性getters/setters 和生命周期方法以外的任何方法之前，
     * 应先调用此方法。以下{@link LifecycleEvent}将按以下顺序触发
     * <ol>
     *   <li>BEFORE_START_EVENT: At the beginning of the method. It is as this
     *                           point the state transitions to
     *                           {@link LifecycleState#STARTING_PREP}.</li>
     *   <li>START_EVENT: During the method once it is safe to call start() for
     *                    any child components. It is at this point that the
     *                    state transitions to {@link LifecycleState#STARTING}
     *                    and that the    methods other than property
     *                    getters/setters and life cycle methods may be
     *                    used.</li>
     *   <li>AFTER_START_EVENT: At the end of the method, immediately before it
     *                          returns. It is at this point that the state
     *                          transitions to {@link LifecycleState#STARTED}.
     *                          </li>
     * </ol>
     *
     * 1. BEFORE_START_EVENT
     * 2.START_EVENT
     * 3.在方法的末尾， start 在返回之前立即返回
     * @throws LifecycleException if this component detects a fatal error
     *                            that prevents this component from being used
     */
    void start() throws LifecycleException;


    /**
     * Gracefully terminate the active use of the    methods other than
     * property getters/setters and life cycle methods of this component. Once
     * the STOP_EVENT is fired, the    methods other than property
     * getters/setters and life cycle methods should not be used. The following
     * {@link LifecycleEvent}s will be fired in the following order:
     * <ol>
     *   <li>BEFORE_STOP_EVENT: At the beginning of the method. It is at this
     *                          point that the state transitions to
     *                          {@link LifecycleState#STOPPING_PREP}.</li>
     *   <li>STOP_EVENT: During the method once it is safe to call stop() for
     *                   any child components. It is at this point that the
     *                   state transitions to {@link LifecycleState#STOPPING}
     *                   and that the    methods other than property
     *                   getters/setters and life cycle methods may no longer be
     *                   used.</li>
     *   <li>AFTER_STOP_EVENT: At the end of the method, immediately before it
     *                         returns. It is at this point that the state
     *                         transitions to {@link LifecycleState#STOPPED}.
     *                         </li>
     * </ol>
     * <p>
     * Note that if transitioning from {@link LifecycleState#FAILED} then the
     * three events above will be fired but the component will transition
     * directly from {@link LifecycleState#FAILED} to
     * {@link LifecycleState#STOPPING}, bypassing
     * {@link LifecycleState#STOPPING_PREP}
     *
     * @throws LifecycleException if this component detects a fatal error
     *                            that needs to be reported
     */
    void stop() throws LifecycleException;

    /**
     * Prepare to discard the object. The following {@link LifecycleEvent}s will
     * be fired in the following order:
     * 准备丢弃该对象。以下{@link LifecycleEvent}将按以下顺序触发：
     * <ol>
     *   <li>DESTROY_EVENT: On the successful completion of component   成功完成组件破坏
     *                      destruction.</li>
     * </ol>
     *
     * @throws LifecycleException if this component detects a fatal error
     *                            that prevents this component from being used
     *                            1.  destroy
     *                            2.  DESTROY_EVENT
     */
    void destroy() throws LifecycleException;


    /**
     * Obtain the current state of the source component.
     *获取源组件的当前状态
     * @return The current state of the source component.
     */
    LifecycleState getState();


    /**
     * Obtain a textual representation of the current component state. Useful
     * for JMX. The format of this string may vary between point releases and
     * should not be relied upon to determine component state. To determine
     * component state, use {@link #getState()}.
     *
     * 获取当前组件状态的文本表示形式。对JMX有用。该字符串的格式在各个点发行版之间可能会有所不同，
     * 因此不应依赖它来确定组件状态。要确定组件状态，请使用{@link #getState（）}。
     * @return The name of the current component state.
     */
    String getStateName();


    /**
     *  Marker interface used to indicate that the instance should only be used
     * once. Calling {@link #stop()} on an instance that supports this interface
     * will automatically call {@link #destroy()} after {@link #stop()}
     * completes.
     *
     * 标记接口用于指示该实例只能使用一次。在支持此接口的实例上调用{@link #stop（）}会在{@link #stop（）}完成后自动调用{@link #destroy（）}。
     */
    interface SingleUse {
    }
}
