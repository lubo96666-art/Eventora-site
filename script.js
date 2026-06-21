
    const cart = new Map();
    const favorites = new Set(JSON.parse(localStorage.getItem('eventoraFavorites') || '[]'));
    let favoriteOnly = false;
    const recent = new Map(JSON.parse(localStorage.getItem('eventoraRecent') || '[]'));
    const compareEvents = new Map();
    const eventRatings = JSON.parse(localStorage.getItem('eventoraRatings') || '{}');
    let pendingReminder = null;
    let pendingPriceAlert = null;

    function currentDiscountPercent(){
        const code = (document.getElementById('promoCode')?.value || '').trim().toUpperCase();
        if(!code) return 0;
        if(code === 'PROMO10') return 10;
        if(code === 'STUDENT15') return 15;
        if(code === 'VIP20') return 20;
        return -1;
    }
    function applyPromoCode(){
        const discount = currentDiscountPercent();
        const text = document.getElementById('promoText');
        if(discount === -1){ text.textContent = 'Невалиден промо код.'; text.style.color = '#b1162b'; }
        else if(discount === 0){ text.textContent = 'Въведи промо код, ако имаш такъв.'; text.style.color = '#687285'; }
        else{ text.textContent = 'Промо кодът е приложен: -' + discount + '%'; text.style.color = '#087645'; }
        renderCart();
    }
    function toggleFavorite(btn){
        const id = btn.dataset.id;
        if(favorites.has(id)) favorites.delete(id); else favorites.add(id);
        localStorage.setItem('eventoraFavorites', JSON.stringify([...favorites]));
        updateFavorites();
    }
    function updateFavorites(){
        document.querySelectorAll('.favorite-btn').forEach(btn=>{
            const active = favorites.has(btn.dataset.id);
            btn.classList.toggle('active', active);
            btn.textContent = active ? '★ Запазено' : '☆ Любими';
        });
    }
    function openModal(id){ closeModals(); document.getElementById(id).classList.add('active'); }
    function closeModals(){ document.querySelectorAll('.modal-backdrop').forEach(m=>m.classList.remove('active')); }
    function switchModal(from,to){ document.getElementById(from).classList.remove('active'); document.getElementById(to).classList.add('active'); }
    document.querySelectorAll('.modal-backdrop').forEach(b=>b.addEventListener('click',e=>{ if(e.target===b) closeModals(); }));

    function isLocalPreview(){ return window.location.protocol === 'file:'; }
    function addToCart(btn){
        const id = btn.dataset.id || 'event-1';
        const title = btn.dataset.title || 'Билет за събитие';
        const price = Number(btn.dataset.price || 75);
        const available = Number(btn.dataset.seats || 0);
        if (available <= 0) { alert('Това събитие е разпродадено.'); return; }
        const qtyInput = document.getElementById('qty-' + id) || btn.closest('.card').querySelector('.qty');
        let qty = Math.max(1, Number((qtyInput && qtyInput.value) || 1));
        qty = Math.min(qty, 8);
        const current = cart.get(id) || {id,title,price,qty:0,available};
        if (current.qty + qty > available) {
            alert('Няма достатъчно свободни билети. Можеш да добавиш общо до ' + available + ' билета за това събитие.');
            return;
        }
        current.qty += qty;
        current.available = available;
        cart.set(id,current);
        renderCart();
        scrollToCart();
    }
    function removeItem(id){ cart.delete(id); renderCart(); }
    function changeCartQty(id, delta){
        const item = cart.get(id);
        if(!item) return;
        const next = item.qty + delta;
        if(next <= 0){ cart.delete(id); renderCart(); return; }
        if(next > item.available){ alert('Няма повече свободни билети за това събитие.'); return; }
        item.qty = next;
        cart.set(id,item);
        renderCart();
    }
    function clearCart(){ if(cart.size && confirm('Да изчистя ли всички билети от кошницата?')){ cart.clear(); renderCart(); } }
    function toggleCart(){
        const box = document.getElementById('cart');
        const btn = document.getElementById('cartToggle');
        const minimized = box.classList.toggle('minimized');
        btn.textContent = minimized ? '+' : '−';
        btn.setAttribute('aria-label', minimized ? 'Отвори кошницата' : 'Минимизирай кошницата');
    }
    function openCart(){
        const box = document.getElementById('cart');
        const btn = document.getElementById('cartToggle');
        box.classList.remove('minimized');
        if(btn){ btn.textContent='−'; btn.setAttribute('aria-label','Минимизирай кошницата'); }
    }
    function renderCart(){
        const holder=document.getElementById('cartItems'), fields=document.getElementById('checkoutFields'); holder.innerHTML=''; fields.innerHTML='';
        let count=0,total=0;
        if(cart.size===0){ holder.className='empty'; holder.textContent='Кошницата е празна.'; }
        else{ holder.className=''; cart.forEach(item=>{ count+=item.qty; total+=item.qty*item.price; const row=document.createElement('div'); row.className='cart-item'; row.innerHTML=`<div><b>${item.title}</b><span class="muted">${item.qty} × €${item.price.toFixed(2)}</span></div><div class="cart-line-actions"><button class="mini-btn" type="button" onclick="changeCartQty('${item.id}',-1)">−</button><button class="mini-btn" type="button" onclick="changeCartQty('${item.id}',1)">+</button><button class="mini-btn" type="button" onclick="removeItem('${item.id}')">×</button></div>`; holder.appendChild(row); fields.insertAdjacentHTML('beforeend', `<input type="hidden" name="eventIds" value="${item.id}"><input type="hidden" name="quantities" value="${item.qty}">`); }); }
        const discount = currentDiscountPercent();
        const validDiscount = discount > 0 ? discount : 0;
        const discountAmount = total * validDiscount / 100;
        const insuranceEnabled = document.getElementById('ticketInsurance')?.checked;
        const insuranceTotal = insuranceEnabled ? count * 2.99 : 0;
        const finalTotal = total - discountAmount + insuranceTotal;
        const loyalty = Math.floor(finalTotal);
        const loyaltyHolder = document.getElementById('loyaltyPoints');
        if(loyaltyHolder) loyaltyHolder.textContent = loyalty + ' т.';
        document.getElementById('cartCount').textContent=count; document.getElementById('cartMini').textContent=count+' билета'; document.getElementById('cartTotal').textContent='€'+finalTotal.toFixed(2);
        document.getElementById('promoCodeHidden').value = validDiscount > 0 ? document.getElementById('promoCode').value.trim().toUpperCase() : '';
        document.getElementById('discountLine').style.display = validDiscount > 0 ? 'flex' : 'none';
        document.getElementById('discountValue').textContent = '-€' + discountAmount.toFixed(2) + (insuranceTotal > 0 ? ' · защита +€' + insuranceTotal.toFixed(2) : '');
    }
    document.getElementById('checkoutForm').addEventListener('submit',function(e){
        if(cart.size===0){ e.preventDefault(); alert('Кошницата е празна.'); return; }
        for (const item of cart.values()) {
            if (item.qty > item.available) {
                e.preventDefault();
                alert('Няма достатъчно свободни билети за: ' + item.title);
                return;
            }
        }
        if(isLocalPreview()){ e.preventDefault(); alert('Кошницата работи. За реална поръчка стартирай Spring Boot и отвори http://localhost:8080/visualization'); }
    });
    document.querySelectorAll('form[action^="/"]').forEach(form=>{ form.addEventListener('submit', function(e){ if(isLocalPreview()){ e.preventDefault(); alert('За регистрация, вход, имейл код и реална поръчка стартирай Spring Boot и отвори http://localhost:8080/visualization'); } }); });
    function scrollToCart(){ openCart(); document.getElementById('cart').scrollIntoView({behavior:'smooth',block:'center'}); }
    function setCategory(category){ document.getElementById('categoryFilter').value = category; filterEvents(); document.getElementById('events').scrollIntoView({behavior:'smooth'}); }
    function filterEvents(){
        const q=document.getElementById('searchInput').value.toLowerCase();
        const c=document.getElementById('categoryFilter').value;
        let visible=0;
        document.querySelectorAll('.event-card').forEach(card=>{
            const okTitle=card.dataset.title.toLowerCase().includes(q);
            const okCat=c==='all'||card.dataset.category===c;
            const okFav=!favoriteOnly || favorites.has(card.querySelector('.favorite-btn')?.dataset.id);
            const show=okTitle&&okCat&&okFav;
            card.style.display=show?'grid':'none';
            if(show) visible++;
        });
        const noResults=document.getElementById('noResults');
        if(noResults){
            if(favoriteOnly && favorites.size === 0){
                noResults.textContent = 'Все още нямаш любими събития. Натисни „☆ Любими“ на събитие, за да го запазиш тук.';
            }else if(favoriteOnly){
                noResults.textContent = 'Няма любими събития, които отговарят на текущото търсене.';
            }else{
                noResults.textContent = 'Няма намерени събития по избраните филтри. Опитай с друга категория или изчисти филтрите.';
            }
            noResults.style.display=visible===0?'block':'none';
        }
    }
    function toggleFavoriteFilter(){
        favoriteOnly=!favoriteOnly;
        const btn=document.getElementById('favoriteFilter');
        btn.classList.toggle('active', favoriteOnly);
        btn.textContent = favoriteOnly ? '★ Показва любими' : '★ Само любими';
        if(favoriteOnly){ const search=document.getElementById('searchInput'); if(search) search.value=''; const category=document.getElementById('categoryFilter'); if(category) category.value='all'; }
        filterEvents();
    }
    function sortEvents(){
        const grid=document.querySelector('.grid');
        const cards=[...document.querySelectorAll('.event-card')];
        const mode=document.getElementById('sortFilter')?.value || 'date';
        cards.sort((a,b)=>{
            if(mode==='priceAsc') return Number(a.dataset.price)-Number(b.dataset.price);
            if(mode==='priceDesc') return Number(b.dataset.price)-Number(a.dataset.price);
            if(mode==='seats') return Number(b.dataset.seats)-Number(a.dataset.seats);
            return String(a.dataset.date).localeCompare(String(b.dataset.date));
        });
        cards.forEach(card=>grid.appendChild(card));
        filterEvents();
    }
    function clearFilters(){
        document.getElementById('searchInput').value='';
        const secondary=document.getElementById('secondarySearch'); if(secondary) secondary.value='';
        document.getElementById('categoryFilter').value='all';
        document.getElementById('sortFilter').value='date';
        favoriteOnly=false;
        const favBtn=document.getElementById('favoriteFilter'); if(favBtn){ favBtn.classList.remove('active'); favBtn.textContent='★ Само любими'; }
        sortEvents();
    }
    function shareEvent(btn){
        const title=btn.dataset.title || 'събитие';
        if(navigator.share){ navigator.share({title:'Eventora', text:'Виж това събитие: '+title, url:window.location.href}).catch(()=>{}); }
        else { navigator.clipboard?.writeText(window.location.href); alert('Линкът към страницата е копиран.'); }
    }
    function subscribeNewsletter(e){
        e.preventDefault();
        const email=document.getElementById('newsletterEmail').value;
        alert('Благодарим! Ще получаваш известия за нови събития на '+email+'.');
        e.target.reset();
    }

    function toggleTheme(){
        const dark = !document.body.classList.contains('dark-mode');
        document.body.classList.toggle('dark-mode', dark);
        localStorage.setItem('eventoraTheme', dark ? 'dark' : 'light');
        const btn=document.getElementById('themeToggle'); if(btn) btn.textContent = dark ? '☀️' : '🌙';
    }

    function toggleCompare(btn){
        const data=btn.dataset;
        if(compareEvents.has(data.id)){ compareEvents.delete(data.id); btn.classList.remove('active'); }
        else {
            if(compareEvents.size >= 3){ alert('Можеш да сравняваш до 3 събития едновременно.'); return; }
            compareEvents.set(data.id,{id:data.id,title:data.title,category:data.category,price:Number(data.price||0),seats:Number(data.seats||0),location:data.location,date:data.dateLabel});
            btn.classList.add('active');
        }
        renderCompareBar();
    }
    function renderCompareBar(){
        const bar=document.getElementById('compareBar');
        const list=document.getElementById('compareList');
        if(!bar || !list) return;
        list.innerHTML='';
        if(compareEvents.size===0){ bar.classList.remove('active'); return; }
        bar.classList.add('active');
        compareEvents.forEach(item=>{ const pill=document.createElement('span'); pill.className='compare-pill'; pill.textContent=item.title; list.appendChild(pill); });
    }
    function clearCompare(){
        compareEvents.clear();
        document.querySelectorAll('.compare-btn').forEach(b=>b.classList.remove('active'));
        renderCompareBar();
        const content=document.getElementById('compareContent'); if(content) content.className='empty', content.textContent='Няма избрани събития.';
    }
    function openCompare(){
        const content=document.getElementById('compareContent');
        if(compareEvents.size===0){ content.className='empty'; content.textContent='Няма избрани събития. Натисни „Сравни“ върху няколко събития.'; openModal('compareModal'); return; }
        content.className='';
        let html='<table class="compare-table"><thead><tr><th>Събитие</th><th>Категория</th><th>Дата</th><th>Локация</th><th>Цена</th><th>Места</th></tr></thead><tbody>';
        compareEvents.forEach(i=>{ html+=`<tr><td><b>${i.title}</b></td><td>${i.category||'-'}</td><td>${i.date||'-'}</td><td>${i.location||'-'}</td><td>€${i.price.toFixed(2)}</td><td>${i.seats}</td></tr>`; });
        html+='</tbody></table>';
        content.innerHTML=html;
        openModal('compareModal');
    }
    function downloadCalendar(btn){
        const data=btn.dataset;
        const start=new Date(data.date);
        const end=new Date(start.getTime()+2*60*60*1000);
        const fmt=d=>d.toISOString().replace(/[-:]/g,'').split('.')[0]+'Z';
        const text=['BEGIN:VCALENDAR','VERSION:2.0','PRODID:-//Eventora//BG','BEGIN:VEVENT','UID:'+Date.now()+'@eventora','DTSTAMP:'+fmt(new Date()),'DTSTART:'+fmt(start),'DTEND:'+fmt(end),'SUMMARY:'+escapeIcs(data.title||'Eventora event'),'LOCATION:'+escapeIcs(data.location||''),'DESCRIPTION:'+escapeIcs(data.description||'Билетно събитие от Eventora'),'END:VEVENT','END:VCALENDAR'].join('\r\n');
        const blob=new Blob([text],{type:'text/calendar;charset=utf-8'});
        const a=document.createElement('a');
        a.href=URL.createObjectURL(blob);
        a.download=(data.title||'eventora-event').replace(/[^a-z0-9а-яА-Я]+/gi,'-')+'.ics';
        document.body.appendChild(a); a.click(); a.remove();
        setTimeout(()=>URL.revokeObjectURL(a.href),1000);
    }
    function escapeIcs(v){
        return String(v)
            .replace(/\\/g, '\\\\')
            .replace(/;/g, '\\;')
            .replace(/,/g, '\\,')
            .replace(/\r?\n/g, '\\n');
    }
    function openReminder(btn){
        pendingReminder={id:btn.dataset.id,title:btn.dataset.title,date:btn.dataset.dateLabel};
        document.getElementById('reminderText').textContent='Напомняне за „'+pendingReminder.title+'“ · '+pendingReminder.date;
        openModal('reminderModal');
    }
    function saveReminder(when){
        if(!pendingReminder) return;
        const reminders=JSON.parse(localStorage.getItem('eventoraReminders') || '[]');
        reminders.push({...pendingReminder,when});
        localStorage.setItem('eventoraReminders', JSON.stringify(reminders));
        closeModals();
        alert('Напомнянето е записано: '+pendingReminder.title+' · '+when);
    }
    function rateEvent(btn, rating){
        const row=btn.closest('.rating-row');
        const id=row?.dataset.id;
        if(!id) return;
        eventRatings[id]=rating;
        localStorage.setItem('eventoraRatings', JSON.stringify(eventRatings));
        paintRating(row, rating);
    }
    function paintRating(row, rating){
        row.querySelectorAll('.star-btn').forEach((b,i)=>b.classList.toggle('active', i<rating));
    }
    function initRatings(){
        document.querySelectorAll('.rating-row').forEach(row=>paintRating(row, Number(eventRatings[row.dataset.id] || 0)));
    }
    function renderSeatMap(available, capacity){
        const holder=document.getElementById('seatMap');
        if(!holder) return;
        holder.innerHTML='';
        const total=40;
        const ratio=capacity ? Math.max(0, Math.min(1, available/capacity)) : .5;
        const free=Math.round(total*ratio);
        for(let i=0;i<total;i++){
            const s=document.createElement('span');
            s.className='seat '+(i>=free?'taken':(i<Math.min(4,free)?'selected':''));
            holder.appendChild(s);
        }
    }

    function toggleGiftMessage(){
        const box=document.getElementById('giftMessageBox');
        const checked=document.getElementById('giftTicket')?.checked;
        if(box) box.classList.toggle('active', !!checked);
    }
    function openPriceAlert(btn){
        pendingPriceAlert={title:btn.dataset.title || 'събитие', price:Number(btn.dataset.price || 0)};
        document.getElementById('priceAlertTitle').textContent='Текуща цена за „'+pendingPriceAlert.title+'“: €'+pendingPriceAlert.price.toFixed(2)+'.';
        document.getElementById('priceAlertValue').value=Math.max(1, Math.floor(pendingPriceAlert.price * .85));
        openModal('priceAlertModal');
    }
    function savePriceAlert(){
        if(!pendingPriceAlert) return;
        const value=Number(document.getElementById('priceAlertValue').value || 0);
        if(value<=0){ alert('Въведи валидна цена.'); return; }
        const alerts=JSON.parse(localStorage.getItem('eventoraPriceAlerts') || '[]');
        alerts.push({...pendingPriceAlert, target:value, createdAt:new Date().toISOString()});
        localStorage.setItem('eventoraPriceAlerts', JSON.stringify(alerts));
        closeModals();
        alert('Ценовият сигнал е записан за „'+pendingPriceAlert.title+'“ при цена €'+value.toFixed(2)+'.');
    }

    function initTheme(){
        const dark = localStorage.getItem('eventoraTheme') === 'dark';
        document.body.classList.toggle('dark-mode', dark);
        const btn=document.getElementById('themeToggle'); if(btn) btn.textContent = dark ? '☀️' : '🌙';
    }
    function openDetails(btn){
        const data = btn.dataset;
        document.getElementById('detailTitle').textContent = data.title || 'Събитие';
        document.getElementById('detailDescription').textContent = data.description || 'Няма описание.';
        document.getElementById('detailLocation').textContent = data.location || '-';
        document.getElementById('detailDate').textContent = data.dateLabel || '-';
        document.getElementById('detailSeats').textContent = (data.seats || 0) + ' свободни места от ' + (data.capacity || '-');
        document.getElementById('detailPrice').textContent = '€' + Number(data.price || 0).toFixed(2);
        const addBtn=document.getElementById('detailAddBtn');
        addBtn.onclick = () => { addToCart(btn); closeModals(); };
        addBtn.disabled = Number(data.seats || 0) <= 0;
        addBtn.textContent = addBtn.disabled ? 'Разпродадено' : 'Добави билет';
        renderCountdown(data.date);
        renderSeatMap(Number(data.seats || 0), Number(data.capacity || 0));
        rememberRecent(data);
        openModal('detailsModal');
    }
    function renderCountdown(dateValue){
        const holder=document.getElementById('detailCountdown');
        const target=new Date(dateValue);
        const diff=Math.max(0,target.getTime()-Date.now());
        const days=Math.floor(diff/86400000);
        const hours=Math.floor(diff/3600000)%24;
        const mins=Math.floor(diff/60000)%60;
        const secs=Math.floor(diff/1000)%60;
        holder.innerHTML=`<div class="count-box"><b>${days}</b><span>дни</span></div><div class="count-box"><b>${hours}</b><span>часа</span></div><div class="count-box"><b>${mins}</b><span>мин</span></div><div class="count-box"><b>${secs}</b><span>сек</span></div>`;
    }
    function rememberRecent(data){
        if(!data.id) return;
        recent.delete(data.id);
        recent.set(data.id,{id:data.id,title:data.title,category:data.category,price:data.price,date:data.dateLabel});
        while(recent.size>6){ recent.delete(recent.keys().next().value); }
        localStorage.setItem('eventoraRecent', JSON.stringify([...recent]));
        renderRecent();
    initRatings();
    renderCompareBar();
    }
    function renderRecent(){
        const section=document.getElementById('recentSection');
        const list=document.getElementById('recentList');
        if(!section || !list) return;
        list.innerHTML='';
        if(recent.size===0){ section.classList.remove('active'); return; }
        section.classList.add('active');
        [...recent.values()].reverse().forEach(item=>{
            const div=document.createElement('div');
            div.className='recent-item';
            div.innerHTML=`<b>${item.title}</b><span>${item.category} · ${item.date}</span><br><span>от €${Number(item.price || 0).toFixed(2)}</span>`;
            div.onclick=()=>{
                const card=[...document.querySelectorAll('.event-card')].find(c=>c.querySelector('.favorite-btn')?.dataset.id===item.id);
                if(card){ card.scrollIntoView({behavior:'smooth',block:'center'}); card.animate([{transform:'scale(1)'},{transform:'scale(1.015)'},{transform:'scale(1)'}],{duration:650}); }
            };
            list.appendChild(div);
        });
    }
    function clearRecent(){ recent.clear(); localStorage.removeItem('eventoraRecent'); renderRecent(); }

    initTheme();
    sortEvents();
    updateFavorites();
    renderRecent();
    initRatings();
    renderCompareBar();
    const mode=document.body.dataset.authMode; if(mode==='login') openModal('loginModal'); if(mode==='register') openModal('registerModal'); if(mode==='forgot') openModal('forgotModal'); if(mode==='verifyCode') openModal('verifyCodeModal'); if(mode==='reset') openModal('resetModal');
