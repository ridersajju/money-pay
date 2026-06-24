const CACHE_NAME = 'money-pay-cache-v1';
const ASSETS_TO_CACHE = [
  './login.html',
  './monthly-spend.html',
  './project.html',
  './manifest.json',
  './icons/icon-192.png',
  './icons/icon-512.png',
  'https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap',
  'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css',
  'https://cdn.jsdelivr.net/npm/chart.js'
];

// Install Event
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => {
        console.log('[Service Worker] Caching app shell and core assets');
        return cache.addAll(ASSETS_TO_CACHE);
      })
      .then(() => self.skipWaiting())
  );
});

// Activate Event
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(cache => {
          if (cache !== CACHE_NAME) {
            console.log('[Service Worker] Clearing old cache storage', cache);
            return caches.delete(cache);
          }
        })
      );
    }).then(() => self.clients.claim())
  );
});

// Fetch Event (Cache-First / Network Fallback Strategy)
self.addEventListener('fetch', event => {
  event.respondWith(
    caches.match(event.request)
      .then(cachedResponse => {
        if (cachedResponse) {
          // Serve cached copy
          return cachedResponse;
        }
        
        // Fetch from network and cache dynamically if it's a web font or external library
        return fetch(event.request).then(response => {
          if (!response || response.status !== 200 || response.type !== 'basic') {
            return response;
          }
          
          // Cache dynamic requests for external assets
          const requestUrl = new URL(event.request.url);
          if (requestUrl.origin === self.location.origin || 
              requestUrl.href.includes('fonts.googleapis.com') ||
              requestUrl.href.includes('cdnjs.cloudflare.com') ||
              requestUrl.href.includes('cdn.jsdelivr.net')) {
            const responseToCache = response.clone();
            caches.open(CACHE_NAME).then(cache => {
              cache.put(event.request, responseToCache);
            });
          }
          
          return response;
        }).catch(err => {
          console.log('[Service Worker] Fetch failed, serving offline page if possible', err);
        });
      })
  );
});
