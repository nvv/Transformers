package com.vnamashko.transformers.ui.vm

import android.arch.lifecycle.ViewModel
import com.vnamashko.transformers.network.model.Transformer
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject



/**
 * @author Vlad Namashko
 */
class TransformerListVM : ViewModel() {

    private val transformerMap = mutableMapOf<String, Transformer>()

    // emmit last available item on subscribe
    val observer = BehaviorSubject.create<List<Transformer>>()

    fun items() = transformerMap.values.toList()

    fun setTransformers(items: List<Transformer>) {
        transformerMap.clear()

        items.forEach {
            it.id?.let { id ->
                transformerMap[id] = it
            }
        }

        observer.onNext(items())
    }

    /**
     * Add or update item in list
     */
    fun addUpdateTransformer(item: Transformer) {
        item.id?.let {id ->
            transformerMap[id] = item
        }

        observer.onNext(items())
    }

    fun deleteTransformer(id: String) {
        transformerMap.remove(id)?.let {
            observer.onNext(items())
        }
    }


}