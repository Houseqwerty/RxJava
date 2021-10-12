package com.example.rxjava

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observables.ConnectableObservable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.AsyncSubject
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import org.reactivestreams.Subscription
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


private const val TAG = "RxJava"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        completable()
//        single()
//        maybe()
//        observable()
//        flowable()
//        flowableBackpressure()
//        coldObservable()
//        publishTest()
//        multicastTest()
//        refCountTest()
//        publishSubject()
//        replaySubject()
//        behaviorSubject()
//        assyncSubject()
    }

    fun completable() {
        val observer1 = object : CompletableObserver {

            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "Completable1: onSubscribe")
                compositeDisposable.add(d)
            }

            override fun onComplete() {
                Log.d(TAG, "Completable1: onComplete")
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "Completable1: onError")
            }

        }
        val observer2 = object : CompletableObserver {

            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "Completable2: onSubscribe")
                compositeDisposable.add(d)
            }

            override fun onComplete() {
                Log.d(TAG, "Completable2: onComplete")
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "Completable2: onError")
            }

        }
        val observable = Completable.fromCallable { server() }

        val disposable1 = observable.subscribe(
            {
                Log.d(TAG, "Completable: onComplete")
            },
            {
                Log.d(TAG, "Completable: ${it.message}")
            }
        )

        observable.subscribe()

//        observable.subscribe(observer1)
//        observable.subscribe(observer2)
/*
        Completable.fromCallable {
            return@fromCallable Log.d(TAG, "Completable: do work")
        }
            .subscribe()*/
    }

    private var disposable: Disposable? = null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        compositeDisposable.dispose()
    }

    fun single() {
        val singleObserver = object : SingleObserver<String> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "Single: onSubscribe")
            }

            override fun onSuccess(item: String) {
                Log.d(TAG, "Single: onSuccess $item")
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "Single: onError ${e.message}")
            }

        }
        /*Single.fromCallable { server() }
            .subscribe(singleObserver)*/
//        val singleObservable = Single.fromCallable { server() }
        val singleObservable = Single.fromCallable { serverNull() }

//        singleObservable.subscribe(singleObserver)
        val disposable: Disposable = singleObservable.subscribe()
    }

    fun serverNull(): String? {
        return null
    }

    @kotlin.jvm.Throws(IOException::class)
    fun doWork(): String {
        throw IOException("ОШибка!!!!!!!!")
    }

    fun onSuccess(item: String) {

    }

    fun onError(throwable: Throwable) {

    }


    fun maybe() {
        val maybeObservable = Maybe.fromCallable<String> { server() }

        val maybeObserver = object : MaybeObserver<String> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "Maybe: onSubscribe")
            }

            override fun onSuccess(t: String) {
                Log.d(TAG, "Maybe: $t")
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "Maybe: ${e.message}")
            }

            override fun onComplete() {
                Log.d(TAG, "Maybe: onComplete")
            }

        }
        maybeObservable.subscribe()
        val disposable = maybeObservable.subscribe(maybeObserver)
    }

    fun observable() {
        val observable = Observable.create<String> { emitter: ObservableEmitter<String> ->
            emitter.onNext("шиммеры")
            emitter.onNext(server())
            emitter.onComplete()
            emitter.onNext("Ещё данные")
        }

        val observer = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "Observable: onSubscribe")
            }

            override fun onNext(t: String) {
                Log.d(TAG, "Observable: onNext $t")
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "Observable: ${e.localizedMessage}")
            }

            override fun onComplete() {
                Log.d(TAG, "Observable: onComplete")
            }

        }
        observable.subscribe(observer)
        /*val observable = Observable.fromCallable { server() }

        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "Observable1: onSubscribe")
                }

                override fun onNext(t: String) {
                    Log.d(TAG, "Observable1: onNext $t")
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "Observable1: ${e.localizedMessage}")
                }

                override fun onComplete() {
                    Log.d(TAG, "Observable1: onComplete")
                }

            })

        observable.subscribe(object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "Observable2: onSubscribe")
            }

            override fun onNext(t: String) {
                Log.d(TAG, "Observable2: onNext $t")
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "Observable2: ${e.localizedMessage}")
            }

            override fun onComplete() {
                Log.d(TAG, "Observable2: onComplete")
            }

        })
*/

        /*subscribe(
                    { Log.d(TAG, "Observable: onNext $it") },
                    { Log.d(TAG, "Observable: ${it.localizedMessage}") },
                    { Log.d(TAG, "Observable: onComplete") }
                )*/
    }

    fun server(): String {
//        throw IOException("Сервер сломался")
        Thread.sleep(3000)
        return "Результат работы сервера ${Calendar.getInstance().timeInMillis}"
    }

    fun flowable() {
        val flowable = Flowable.range(0, 1000)
            .map(Int::toString)
        var subscription: Subscription? = null
        val observer = object : FlowableSubscriber<String> {

            override fun onSubscribe(s: Subscription) {
                Log.d(TAG, "Flowable: onSubscribe")
                subscription = s
                subscription?.request(500)
            }

            override fun onNext(t: String) {
                Log.d(TAG, "Flowable: onNext $t")
//                Thread.sleep(3)
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "Flowable: ${e.localizedMessage}")
            }

            override fun onComplete() {
                Log.d(TAG, "Flowable: onComplete")
            }

        }
        flowable.subscribe(observer)
        do {
            Thread.sleep(5000)
            subscription?.request(300)
        } while (true)
    }

    fun flowableBackpressure() {
        val source = PublishProcessor.create<Int>()

        val subscription = source
            .onBackpressureBuffer()
            .observeOn(Schedulers.computation())
            .subscribe(ComputeFunction::compute, Throwable::printStackTrace)
        for (i in 0..100000) {
            source.onNext(i)
        }
    }

    object ComputeFunction {
        fun compute(v: Int) {
            try {
                Log.d(TAG, "compute integer v: $v")
                Thread.sleep(v.toLong() * 1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    fun strings(): List<String> {
        val result = mutableListOf<String>()
        for (i in 0 until 10) {
            result.add(i.toString())
        }
        return result
    }

    @Throws(InterruptedException::class)
    fun coldObservable() {
        val myObservable = Observable.interval(2, TimeUnit.SECONDS)

        myObservable.subscribe { item -> Log.d(TAG, "Observer 1: $item") }
        Thread.sleep(3000)
        myObservable.subscribe { item -> Log.d(TAG, "Observer 2: $item") }
        Thread.sleep(5000)
    }

    @Throws(InterruptedException::class)
    fun publishTest() {
        val myObservable = Observable.interval(1, TimeUnit.SECONDS)
            .take(10)
        val connectableObservable: ConnectableObservable<Long> = myObservable.publish()
        connectableObservable.connect()
        Thread.sleep(5000)
        connectableObservable.subscribe { item -> Log.d(TAG, "Observer 1: $item") }
    }

    @Throws(InterruptedException::class)
    fun multicastTest() {
        val myObservable = Observable.interval(1, TimeUnit.SECONDS)
            .take(10)
        val connectableObservable = myObservable.publish()
        // первый: 0...9
        // второй: 1...9
        // второй: 5...9
        connectableObservable.subscribe { item: Long -> Log.d(TAG, "Observer 1: $item") }
        Thread.sleep(1000)
        connectableObservable.connect()
        Thread.sleep(2000)
        connectableObservable.subscribe { item: Long -> Log.d(TAG, "Observer 2: $item") }
        Thread.sleep(3000)
        connectableObservable.subscribe { item: Long -> Log.d(TAG, "Observer 3: $item") }
    }

    @Throws(InterruptedException::class)
    fun refCountTest() {
        val myObservable = Observable.interval(1, TimeUnit.SECONDS).take(10)
            .flatMap { item -> Observable.just(item)  }
            .map { item -> item + 1 }

        val hotObservable = myObservable.publish().refCount(2, 5, TimeUnit.SECONDS)

        val subscription1 = hotObservable
            .doOnSubscribe { d -> Log.d(TAG, "Observer 1 subscribed") }
            .doFinally { Log.d(TAG, "Observer 1 unsubscribed") }
            .subscribe { item -> Log.d(TAG, "Observer 1: $item") }

        Thread.sleep(3000)

        val subscription2 = hotObservable
            .doOnSubscribe { d -> Log.d(TAG, "Observer 2 subscribed") }
            .doFinally { Log.d(TAG, "Observer 2 unsubscribed") }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { item -> Log.d(TAG, "Observer 2: $item") }

        Thread.sleep(3000)
        subscription1.dispose()
        subscription2.dispose()
        Thread.sleep(3000)
        hotObservable
            .doOnSubscribe { d -> Log.d(TAG, "Observer 3 subscribed") }
            .doFinally { Log.d(TAG, "Observer 3 unsubscribed") }
            .subscribe { item -> Log.d(TAG, "Observer 3: $item") }
/*        Thread.sleep(3000)
        hotObservable
            .doOnSubscribe { d -> Log.d(TAG, "Observer 4 subscribed") }
            .doFinally { Log.d(TAG, "Observer 4 unsubscribed") }
            .subscribe { item -> Log.d(TAG, "Observer 4: $item") }*/
    }

    fun publishSubject() {
        val source = PublishSubject.create<Int>()

        // Получит 1, 2, 3, 4 и onComplete
        source.subscribe { item -> Log.d(TAG, "Observer 1: $item") }

        source.onNext(1)
        source.onNext(2)
        source.onNext(3)

        // Получит 4 и onComplete для следующего наблюдателя тоже.source.subscribe(getSecondObserver());
        source.subscribe { item -> Log.d(TAG, "Observer 2: $item") }

        // Получит 4 и onComplete для следующего наблюдателя тоже.source.subscribe(getSecondObserver());
        source.onNext(4)
        source.onComplete()
    }

    fun replaySubject() {
        val source = ReplaySubject.create<Int>()
        // Он получит 1, 2, 3, 4
        source.subscribe { item -> Log.d(TAG, "Observer 1: $item") }
        source.onNext(1)
        source.onNext(2)
        source.onNext(3)
        source.onNext(4)
        // Он также получит 1, 2, 3, 4 так как он использует Replay Subject
        source.subscribe { item -> Log.d(TAG, "Observer 2: $item") }
        source.onNext(5)
        source.onComplete()
    }

    fun behaviorSubject() {
        val source = BehaviorSubject.create<Int>()

// Получит 1, 2, 3, 4 and onComplete
        source.subscribe { item -> Log.d(TAG, "Observer 1: $item") }
        source.onNext(1)
        source.onNext(2)
        source.onNext(3)
// Получит 3(последний элемент) и 4(последующие элементы) и onComplete
        source.subscribe { item -> Log.d(TAG, "Observer 2: $item") }
        source.onNext(4)
        source.onComplete()
    }

    fun assyncSubject() {
        val source = AsyncSubject.create<Int>()
// Получит только 4 и onComplete
        source.subscribe { item -> Log.d(TAG, "Observer 1: $item") }

        source.onNext(1)
        source.onNext(2)
        source.onNext(3)
// Тоже получит только 4 и onComplete
        source.subscribe { item -> Log.d(TAG, "Observer 2: $item") }
        source.onNext(4)

        // Без onComplete не с эмитится пооследнее значение
        source.onComplete()
    }

    private class SomeException : Exception()

    fun logCurrentThread(where: String) {
        Log.d(TAG, where + " " + Thread.currentThread().name)
    }
}