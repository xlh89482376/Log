package com.zhidao.logcat.service;

/**
 * AIDL for LogcatViewerService service.
 */
public interface ILogcatViewerService extends android.os.IInterface
{
    /** Default implementation for ILogcatViewerService. */
    class Default implements com.zhidao.logcat.service.ILogcatViewerService
    {

        @Override public void changeLogcatSource(java.lang.String buffer) throws android.os.RemoteException
        {
        }

        @Override public void restart() throws android.os.RemoteException
        {
        }

        @Override public void stop() throws android.os.RemoteException
        {
        }

        @Override public void startRecording(java.lang.String logFilename, java.lang.String filterText) throws android.os.RemoteException
        {
        }
   
        @Override public void stopRecording() throws android.os.RemoteException
        {
        }
   
        @Override public boolean isRecording() throws android.os.RemoteException
        {
            return false;
        }

        @Override public void pause() throws android.os.RemoteException
        {
        }

        @Override public void resume() throws android.os.RemoteException
        {
        }
        @Override
        public android.os.IBinder asBinder() {
            return null;
        }
    }

    abstract class Stub extends android.os.Binder implements com.zhidao.logcat.service.ILogcatViewerService
    {
        private static final java.lang.String DESCRIPTOR = "com.fatangare.logcatviewer.service.ILogcatViewerService";
        /** Construct the stub at attach it to the interface. */
        public Stub()
        {
            this.attachInterface(this, DESCRIPTOR);
        }
        /**
         * Cast an IBinder object into an com.fatangare.logcatviewer.service.ILogcatViewerService interface,
         * generating a proxy if needed.
         */
        public static com.zhidao.logcat.service.ILogcatViewerService asInterface(android.os.IBinder obj)
        {
            if ((obj==null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin instanceof ILogcatViewerService))) {
                return ((com.zhidao.logcat.service.ILogcatViewerService)iin);
            }
            return (ILogcatViewerService) new Proxy(obj);
        }
        @Override public android.os.IBinder asBinder()
        {
            return this;
        }
        @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
        {
            java.lang.String descriptor = DESCRIPTOR;
            switch (code)
            {
                case INTERFACE_TRANSACTION:
                {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_changeLogcatSource:
                {
                    data.enforceInterface(descriptor);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    this.changeLogcatSource(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_restart:
                {
                    data.enforceInterface(descriptor);
                    this.restart();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_stop:
                {
                    data.enforceInterface(descriptor);
                    this.stop();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_startRecording:
                {
                    data.enforceInterface(descriptor);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    java.lang.String _arg1;
                    _arg1 = data.readString();
                    this.startRecording(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_stopRecording:
                {
                    data.enforceInterface(descriptor);
                    this.stopRecording();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_isRecording:
                {
                    data.enforceInterface(descriptor);
                    boolean _result = this.isRecording();
                    reply.writeNoException();
                    reply.writeInt(((_result)?(1):(0)));
                    return true;
                }
                case TRANSACTION_pause:
                {
                    data.enforceInterface(descriptor);
                    this.pause();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_resume:
                {
                    data.enforceInterface(descriptor);
                    this.resume();
                    reply.writeNoException();
                    return true;
                }
                default:
                {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }
        private static class Proxy implements com.zhidao.logcat.service.ILogcatViewerService
        {
            private final android.os.IBinder mRemote;
            Proxy(android.os.IBinder remote)
            {
                mRemote = remote;
            }
            @Override public android.os.IBinder asBinder()
            {
                return mRemote;
            }
            public java.lang.String getInterfaceDescriptor()
            {
                return DESCRIPTOR;
            }

            @Override public void changeLogcatSource(java.lang.String buffer) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(buffer);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_changeLogcatSource, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().changeLogcatSource(buffer);
                        return;
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override public void restart() throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_restart, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().restart();
                        return;
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
 
            @Override public void stop() throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_stop, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().stop();
                        return;
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
 
            @Override public void startRecording(java.lang.String logFilename, java.lang.String filterText) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(logFilename);
                    _data.writeString(filterText);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_startRecording, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().startRecording(logFilename, filterText);
                        return;
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
 
            @Override public void stopRecording() throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_stopRecording, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().stopRecording();
                        return;
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
   
            @Override public boolean isRecording() throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_isRecording, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().isRecording();
                    }
                    _reply.readException();
                    _result = (0!=_reply.readInt());
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
    
            @Override public void pause() throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_pause, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().pause();
                        return;
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
     
            @Override public void resume() throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_resume, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().resume();
                        return;
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            public static com.zhidao.logcat.service.ILogcatViewerService sDefaultImpl;
        }
        static final int TRANSACTION_changeLogcatSource = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_restart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_stop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
        static final int TRANSACTION_startRecording = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
        static final int TRANSACTION_stopRecording = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
        static final int TRANSACTION_isRecording = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
        static final int TRANSACTION_pause = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
        static final int TRANSACTION_resume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
        public static boolean setDefaultImpl(com.zhidao.logcat.service.ILogcatViewerService impl) {
            if (Stub.Proxy.sDefaultImpl == null && impl != null) {
                Stub.Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }
        public static com.zhidao.logcat.service.ILogcatViewerService getDefaultImpl() {
            return Stub.Proxy.sDefaultImpl;
        }
    }

    void changeLogcatSource(java.lang.String buffer) throws android.os.RemoteException;

    void restart() throws android.os.RemoteException;

    void stop() throws android.os.RemoteException;

    void startRecording(java.lang.String logFilename, java.lang.String filterText) throws android.os.RemoteException;

    void stopRecording() throws android.os.RemoteException;

    boolean isRecording() throws android.os.RemoteException;
 
    void pause() throws android.os.RemoteException;

    void resume() throws android.os.RemoteException;
}

