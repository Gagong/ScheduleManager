import SockJS from 'sockjs-client';
import {Client} from '@stomp/stompjs';

class WebSocketPlugin {
    constructor() {
        this.client = null;
        this.connected = false;
        this.subscriptions = new Map();
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectTimeout = null;
    }

    connect(url = process.env.VITE_WS_URL || '/ws') {
        return new Promise((resolve, reject) => {
            if (this.client && this.connected) {
                resolve(true);
                return;
            }

            this.client = new Client({
                webSocketFactory: () => new SockJS(url),
                debug: process.env.NODE_ENV === 'development' ? (str) => {
                    console.log('STOMP:', str);
                } : () => {},
                reconnectDelay: 5000,
                heartbeatIncoming: 4000,
                heartbeatOutgoing: 4000,
                onConnect: () => {
                    console.log('WebSocket connected');
                    this.connected = true;
                    this.reconnectAttempts = 0;
                    this.restoreSubscriptions();
                    resolve(true);
                },
                onDisconnect: () => {
                    console.log('WebSocket disconnected');
                    this.connected = false;
                },
                onStompError: (frame) => {
                    console.error('STOMP error:', frame);
                    this.connected = false;
                    reject(frame);
                },
                onWebSocketError: (error) => {
                    console.error('WebSocket error:', error);
                    this.connected = false;
                    this.handleReconnect(url, resolve, reject);
                }
            });

            this.client.activate();
        });
    }

    handleReconnect(url, resolve, reject) {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            const delay = 5000 * this.reconnectAttempts;

            console.log(`Reconnecting in ${delay}ms (attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts})`);

            this.reconnectTimeout = setTimeout(() => {
                this.connect(url).then(resolve).catch(reject);
            }, delay);
        } else {
            console.error('Max reconnection attempts reached');
            reject(new Error('Max reconnection attempts reached'));
        }
    }

    disconnect() {
        if (this.reconnectTimeout) {
            clearTimeout(this.reconnectTimeout);
        }

        if (this.client && this.connected) {
            this.client.deactivate();
            this.connected = false;
            this.subscriptions.clear();
        }
    }

    subscribe(topic, callback, id = null) {
        if (!this.client || !this.connected) {
            console.warn('Cannot subscribe: not connected');
            return null;
        }

        const subscription = this.client.subscribe(topic, (message) => {
            try {
                const data = JSON.parse(message.body);
                callback(data, message);
            } catch (e) {
                callback(message.body, message);
            }
        });

        const subscriptionId = id || topic;
        this.subscriptions.set(subscriptionId, {
            topic,
            callback,
            subscription
        });

        return subscription;
    }

    unsubscribe(id) {
        const sub = this.subscriptions.get(id);
        if (sub && sub.subscription) {
            sub.subscription.unsubscribe();
            this.subscriptions.delete(id);
        }
    }

    restoreSubscriptions() {
        this.subscriptions.forEach((value, key) => {
            console.log(`Restoring subscription: ${key} to ${value.topic}`);
            const newSubscription = this.client.subscribe(value.topic, (message) => {
                try {
                    const data = JSON.parse(message.body);
                    value.callback(data, message);
                } catch (e) {
                    value.callback(message.body, message);
                }
            });

            value.subscription = newSubscription;
        });
    }

    send(destination, message) {
        if (!this.client || !this.connected) {
            console.warn('Cannot send: not connected');
            return false;
        }

        try {
            const payload = typeof message === 'string' ? message : JSON.stringify(message);
            this.client.publish({
                destination: destination,
                body: payload
            });
            return true;
        } catch (error) {
            console.error('Send error:', error);
            return false;
        }
    }

    on(event, handler) {
        if (!this.messageHandlers) {
            this.messageHandlers = new Map();
        }
        if (!this.messageHandlers.has(event)) {
            this.messageHandlers.set(event, []);
        }
        this.messageHandlers.get(event).push(handler);
    }

    emit(event, data) {
        const handlers = this.messageHandlers?.get(event);
        if (handlers) {
            handlers.forEach(handler => handler(data));
        }
    }
}

const websocket = new WebSocketPlugin();

const WebSocketVuePlugin = {
    install(VueInstance, options = {}) {
        VueInstance.prototype.$websocket = websocket;
        VueInstance.websocket = websocket;

        if (options.autoConnect !== false) {
            const url = options.url || process.env.VITE_WS_URL || '/ws';
            websocket.connect(url).catch(error => {
                console.error('Auto-connect failed:', error);
            });
        }
    }
};

export default WebSocketVuePlugin;