package com.example.rxjava

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.rxjava.databinding.ActivityMainBinding
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
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
//        completable()
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
        assyncSubject()
    }

    fun completable() {
        Completable.fromCallable {
            return@fromCallable Log.d(TAG, "Completable: do work")
        }
            .subscribe {

            }
    }

    fun single() {
        val disposable = Single.just("")
            .subscribe { item, throwable: Throwable? ->
                println()
            }
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
        val disposable = Maybe.fromCallable {
            val result = Log.d(TAG, "Я выполняюсь").toString()
            return@fromCallable result
        }
            .subscribe(object : MaybeObserver<String> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "Maybe: onSubscribe")
                }

                override fun onSuccess(t: String) {
                    Log.d(TAG, "Maybe: $t")
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "Maybe: ${e.localizedMessage}")
                }

                override fun onComplete() {
                    Log.d(TAG, "Maybe: onComplete")
                }

            })
    }

    fun observable() {
        /*Observable.create<String> { emitter: ObservableEmitter<String> ->
            emitter.onNext("шиммеры")
            Thread.sleep(3000)
            emitter.onNext("Новый список")
            emitter.onComplete()
        }
            .subscribe(object : Observer<String> {
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

                })*/
        val observable = Observable.fromCallable { server() }

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


        /*subscribe(
                    { Log.d(TAG, "Observable: onNext $it") },
                    { Log.d(TAG, "Observable: ${it.localizedMessage}") },
                    { Log.d(TAG, "Observable: onComplete") }
                )*/
    }

    fun server(): String {
        Thread.sleep(3000)
        return "Результат работы сервера ${Calendar.getInstance().timeInMillis}"
    }

    fun flowable() {
        Flowable.range(0, 1000)
            .map(Int::toString)
            .subscribe(object : FlowableSubscriber<String> {

                override fun onSubscribe(s: Subscription) {
                    s.request(1000)
                    Log.d(TAG, "Flowable: onSubscribe")
                }

                override fun onNext(t: String) {
                    Log.d(TAG, "Flowable: onNext $t")
                    Thread.sleep(3000)
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "Flowable: ${e.localizedMessage}")
                }

                override fun onComplete() {
                    Log.d(TAG, "Flowable: onComplete")
                }

            })
    }

    fun flowableBackpressure() {
        val source = PublishProcessor.create<Int>()

        source
            .observeOn(Schedulers.computation())
            .subscribe(ComputeFunction::compute, Throwable::printStackTrace)
        for (i in 0..1000) source.onNext(i)
    }

    object ComputeFunction {
        fun compute(v: Int) {
            try {
                Log.d(TAG, "compute integer v: $v")
                Thread.sleep(1000)
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
        val myObservable = Observable.interval(1, TimeUnit.SECONDS)

        myObservable.subscribe { item -> Log.d(TAG, "Observer 1: $item") }
        Thread.sleep(3000)
        myObservable.subscribe { item -> Log.d(TAG, "Observer 2: $item") }
        Thread.sleep(5000)
    }

    @Throws(InterruptedException::class)
    fun publishTest() {
        val myObservable = Observable.interval(1, TimeUnit.SECONDS)
        val connectableObservable = myObservable.publish()
        connectableObservable.connect()
        Thread.sleep(5000)
        connectableObservable.subscribe { item -> Log.d(TAG, "Observer 1: $item") }
    }

    @Throws(InterruptedException::class)
    fun multicastTest() {
        val myObservable = Observable.interval(1, TimeUnit.SECONDS)
            .take(10)
        val connectableObservable = myObservable.publish()
        connectableObservable.subscribe { item: Long -> Log.d(TAG, "Observer 1: $item") }
        connectableObservable.connect()
        Thread.sleep(3000)
        connectableObservable.subscribe { item: Long -> Log.d(TAG, "Observer 2: $item") }
        Thread.sleep(5000)
        connectableObservable.subscribe { item: Long -> Log.d(TAG, "Observer 3: $item") }
    }

    @Throws(InterruptedException::class)
    fun refCountTest() {
        val myObservable = Observable.interval(1, TimeUnit.SECONDS)
        val hotObservable = myObservable.publish().refCount()

        val subscription1 = hotObservable
            .doOnSubscribe { d -> Log.d(TAG, "Observer 1 subscribed") }
            .doFinally { Log.d(TAG, "Observer 1 unsubscribed") }
            .subscribe { item -> Log.d(TAG, "Observer 1: $item") }

        Thread.sleep(3000)

        val subscription2 = hotObservable
            .doOnSubscribe { d -> Log.d(TAG, "Observer 2 subscribed") }
            .doFinally { Log.d(TAG, "Observer 2 unsubscribed") }
            .subscribe { item -> Log.d(TAG, "Observer 2: $item") }

        Thread.sleep(3000)
        subscription1.dispose()
        subscription2.dispose()
        hotObservable
            .doOnSubscribe { d -> Log.d(TAG, "Observer 3 subscribed") }
            .doFinally { Log.d(TAG, "Observer 3 unsubscribed") }
            .subscribe { item -> Log.d(TAG, "Observer 3: $item") }
        Thread.sleep(3000)
        hotObservable
            .doOnSubscribe { d -> Log.d(TAG, "Observer 4 subscribed") }
            .doFinally { Log.d(TAG, "Observer 4 unsubscribed") }
            .subscribe { item -> Log.d(TAG, "Observer 4: $item") }
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