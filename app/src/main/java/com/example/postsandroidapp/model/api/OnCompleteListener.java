package com.example.postsandroidapp.model.api;

public interface OnCompleteListener<T, E> {
    void onSuccess(T response);

    void onFail(E response, int code);
}
