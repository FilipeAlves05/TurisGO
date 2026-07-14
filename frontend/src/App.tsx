import {
  Award,
  CalendarDays,
  Camera,
  CheckCircle2,
  ChevronRight,
  Compass,
  Heart,
  Home,
  LogOut,
  Map,
  MapPin,
  Route,
  Search,
  Star,
  TableProperties,
  UserRound
} from "lucide-react";
import { FormEvent, useCallback, useEffect, useMemo, useState } from "react";
import { getApiError } from "./lib/api";
import { achievementService } from "./services/achievementService";
import { attractionService } from "./services/attractionService";
import { authService } from "./services/authService";
import { checkinService } from "./services/checkinService";
import { favoriteService } from "./services/favoriteService";
import { itineraryService } from "./services/itineraryService";
import { pointOfInterestService } from "./services/pointOfInterestService";
import { reviewService } from "./services/reviewService";
import { trailService } from "./services/trailService";
import { userService } from "./services/userService";
import type {
  Achievement,
  AuthResponse,
  CheckIn,
  Itinerary,
  PointOfInterest,
  Review,
  Tourist,
  TouristAttraction,
  Trail,
  UserType
} from "./types";

type Notice = { type: "success" | "error"; text: string } | null;
type Page =
  | "home"
  | "attractions"
  | "trails"
  | "itineraries"
  | "favorites"
  | "profile"
  | "achievements"
  | "admin";

const emptyAttraction: TouristAttraction = {
  name: "",
  description: "",
  address: "",
  contact: "",
  status: "ATIVO",
  operatingHours: "",
  gallery: "",
  profileImage: ""
};

function currentHash() {
  return window.location.hash.replace(/^#\/?/, "") || "home";
}

function go(route: string) {
  window.location.hash = route;
}

function useRoute() {
  const [route, setRoute] = useState(currentHash);

  useEffect(() => {
    const sync = () => setRoute(currentHash());
    window.addEventListener("hashchange", sync);
    return () => window.removeEventListener("hashchange", sync);
  }, []);

  return route;
}

function crop(text = "", size = 120) {
  return text.length > size ? `${text.slice(0, size).trim()}...` : text;
}

function formatDate(value?: string) {
  if (!value) return "Sem data";
  return new Intl.DateTimeFormat("pt-BR", { dateStyle: "short" }).format(new Date(`${value}T00:00:00`));
}

function formatDateTime(value?: string) {
  if (!value) return "Sem data";
  return new Intl.DateTimeFormat("pt-BR", { dateStyle: "short", timeStyle: "short" }).format(new Date(value));
}

function imageList(attraction?: TouristAttraction) {
  const raw = [attraction?.profileImage, attraction?.gallery].filter(Boolean).join(",");
  return raw
    .split(/[\n,;]+/)
    .map((item) => item.trim())
    .filter(Boolean);
}

function mainImage(attraction: TouristAttraction) {
  return imageList(attraction)[0];
}

export default function App() {
  const route = useRoute();
  const [user, setUser] = useState<AuthResponse | null>(null);
  const [checkingSession, setCheckingSession] = useState(true);
  const [notice, setNotice] = useState<Notice>(null);

  useEffect(() => {
    authService
      .me()
      .then((session) => {
        setUser(session);
        if (!window.location.hash) go(session.type === "INSTITUTION" ? "admin" : "home");
      })
      .catch(() => setUser(null))
      .finally(() => setCheckingSession(false));
  }, []);

  async function logout() {
    await authService.logout().catch(() => undefined);
    setUser(null);
    go("home");
  }

  if (checkingSession) return <Splash />;
  if (!user) return <AuthScreen onLogin={setUser} />;

  return (
    <div className="app-shell">
      <Sidebar user={user} route={route} onLogout={logout} />
      <main className="content">
        {notice && <NoticeBar notice={notice} onClose={() => setNotice(null)} />}
        {user.type === "INSTITUTION" ? (
          <InstitutionAdmin user={user} setNotice={setNotice} />
        ) : (
          <TouristRoutes user={user} route={route} setNotice={setNotice} />
        )}
      </main>
      <MobileNav user={user} route={route} />
    </div>
  );
}

function Splash() {
  return (
    <main className="splash">
      <div className="brand-mark">TG</div>
      <strong>TurisGO</strong>
    </main>
  );
}

function AuthScreen({ onLogin }: { onLogin: (user: AuthResponse) => void }) {
  const [mode, setMode] = useState<"login" | "register">("login");
  const [type, setType] = useState<UserType>("TOURIST");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    birthDate: "",
    cnpj: ""
  });

  async function submit(event: FormEvent) {
    event.preventDefault();
    setLoading(true);
    setError("");

    try {
      if (mode === "register" && type === "TOURIST") {
        await authService.registerTourist({
          name: form.name,
          email: form.email,
          password: form.password,
          birthDate: form.birthDate
        });
      }

      if (mode === "register" && type === "INSTITUTION") {
        await authService.registerInstitution({
          name: form.name,
          email: form.email,
          password: form.password,
          cnpj: form.cnpj
        });
      }

      const session = await authService.login({ email: form.email, password: form.password });
      onLogin(session);
      go(session.type === "INSTITUTION" ? "admin" : "home");
    } catch (err) {
      setError(getApiError(err));
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-hero">
        <div className="brand-mark">TG</div>
        <h1>TurisGO</h1>
        <p>Turismo local com rotas, favoritos, check-ins, avaliações e conquistas.</p>
      </section>
      <section className="auth-panel">
        <div className="segmented">
          <button className={mode === "login" ? "active" : ""} onClick={() => setMode("login")}>
            Entrar
          </button>
          <button className={mode === "register" ? "active" : ""} onClick={() => setMode("register")}>
            Criar conta
          </button>
        </div>
        <form className="form" onSubmit={submit}>
          {mode === "register" && (
            <>
              <label>
                Nome
                <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
              </label>
              <div className="segmented compact">
                <button type="button" className={type === "TOURIST" ? "active" : ""} onClick={() => setType("TOURIST")}>
                  Turista
                </button>
                <button type="button" className={type === "INSTITUTION" ? "active" : ""} onClick={() => setType("INSTITUTION")}>
                  Instituição
                </button>
              </div>
              {type === "TOURIST" ? (
                <label>
                  Data de nascimento
                  <input
                    type="date"
                    value={form.birthDate}
                    onChange={(e) => setForm({ ...form, birthDate: e.target.value })}
                    required
                  />
                </label>
              ) : (
                <label>
                  CNPJ
                  <input value={form.cnpj} onChange={(e) => setForm({ ...form, cnpj: e.target.value })} required />
                </label>
              )}
            </>
          )}
          <label>
            E-mail
            <input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
          </label>
          <label>
            Senha
            <input
              type="password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
            />
          </label>
          {error && <p className="error-text">{error}</p>}
          <button className="primary-button" disabled={loading}>
            {loading ? "Aguarde..." : mode === "login" ? "Entrar" : "Criar e entrar"}
          </button>
        </form>
      </section>
    </main>
  );
}

function Sidebar({ user, route, onLogout }: { user: AuthResponse; route: string; onLogout: () => void }) {
  const items = navItems(user.type);
  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <div className="brand-mark small">TG</div>
        <div>
          <strong>TurisGO</strong>
          <span>{user.name}</span>
        </div>
      </div>
      <nav>
        {items.map((item) => (
          <button key={item.route} className={`nav-button ${route.startsWith(item.route) ? "active" : ""}`} onClick={() => go(item.route)}>
            <item.icon size={19} />
            <span>{item.label}</span>
          </button>
        ))}
      </nav>
      <button className="nav-button logout" onClick={onLogout}>
        <LogOut size={19} />
        <span>Sair</span>
      </button>
    </aside>
  );
}

function MobileNav({ user, route }: { user: AuthResponse; route: string }) {
  return (
    <nav className="mobile-nav">
      {navItems(user.type)
        .slice(0, 5)
        .map((item) => (
          <button key={item.route} className={route.startsWith(item.route) ? "active" : ""} onClick={() => go(item.route)}>
            <item.icon size={19} />
            <span>{item.label}</span>
          </button>
        ))}
    </nav>
  );
}

function navItems(type: UserType) {
  if (type === "INSTITUTION") return [{ route: "admin" as Page, label: "Painel", icon: TableProperties }];
  return [
    { route: "home" as Page, label: "Início", icon: Home },
    { route: "attractions" as Page, label: "Pontos", icon: MapPin },
    { route: "trails" as Page, label: "Trilhas", icon: Route },
    { route: "itineraries" as Page, label: "Roteiros", icon: Map },
    { route: "favorites" as Page, label: "Favoritos", icon: Heart },
    { route: "profile" as Page, label: "Perfil", icon: UserRound },
    { route: "achievements" as Page, label: "Conquistas", icon: Award }
  ];
}

function TouristRoutes({ user, route, setNotice }: { user: AuthResponse; route: string; setNotice: (notice: Notice) => void }) {
  const detail = route.match(/^(attraction|trail)\/(\d+)/);
  if (detail?.[1] === "attraction") return <AttractionDetail id={Number(detail[2])} user={user} setNotice={setNotice} />;
  if (detail?.[1] === "trail") return <TrailDetail id={Number(detail[2])} user={user} setNotice={setNotice} />;

  if (route === "attractions") return <AttractionsPage user={user} setNotice={setNotice} />;
  if (route === "trails") return <TrailsPage user={user} setNotice={setNotice} />;
  if (route === "itineraries") return <ItineraryBuilderPage user={user} setNotice={setNotice} />;
  if (route === "favorites") return <FavoritesPage user={user} setNotice={setNotice} />;
  if (route === "profile") return <ProfilePage user={user} setNotice={setNotice} />;
  if (route === "achievements") return <AchievementsPage user={user} setNotice={setNotice} />;
  return <HomePage user={user} setNotice={setNotice} />;
}

function HomePage({ user, setNotice }: { user: AuthResponse; setNotice: (notice: Notice) => void }) {
  const [query, setQuery] = useState("");
  const [attractions, setAttractions] = useState<TouristAttraction[]>([]);
  const [trails, setTrails] = useState<Trail[]>([]);
  const [favAttractions, setFavAttractions] = useState<TouristAttraction[]>([]);
  const [favTrails, setFavTrails] = useState<Trail[]>([]);

  useEffect(() => {
    Promise.all([attractionService.list(), trailService.list(), favoriteService.listAttractions(user.id), favoriteService.listTrails(user.id)])
      .then(([a, t, fa, ft]) => {
        setAttractions(a);
        setTrails(t);
        setFavAttractions(fa);
        setFavTrails(ft);
      })
      .catch((err) => setNotice({ type: "error", text: getApiError(err) }));
  }, [setNotice, user.id]);

  const filtered = attractions.filter((item) => item.name.toLowerCase().includes(query.toLowerCase()));

  return (
    <>
      <Header title="Início" subtitle="Explore lugares, trilhas e favoritos." />
      <SearchBox value={query} onChange={setQuery} placeholder="Pesquisar pontos turísticos" />
      <SectionTitle title="Pontos turísticos em destaque" />
      <div className="card-grid">{filtered.slice(0, 6).map((item) => <AttractionCard key={item.id} attraction={item} />)}</div>
      <SectionTitle title="Trilhas em destaque" />
      <div className="list-grid">{trails.slice(0, 4).map((trail) => <TrailCard key={trail.id} trail={trail} />)}</div>
      <SectionTitle title="Seus favoritos" />
      {favAttractions.length || favTrails.length ? (
        <div className="list-grid">
          {favAttractions.slice(0, 3).map((item) => <MiniAttraction key={item.id} attraction={item} />)}
          {favTrails.slice(0, 3).map((trail) => <TrailCard key={trail.id} trail={trail} />)}
        </div>
      ) : (
        <EmptyState icon={Compass} title="Explore novos locais" text="Favorite pontos turísticos e trilhas para encontrá-los aqui." />
      )}
    </>
  );
}

function AttractionsPage({ user, setNotice }: { user: AuthResponse; setNotice: (notice: Notice) => void }) {
  const [query, setQuery] = useState("");
  const [items, setItems] = useState<TouristAttraction[]>([]);
  const [favorites, setFavorites] = useState<TouristAttraction[]>([]);

  const load = useCallback(() => {
    Promise.all([attractionService.list(), favoriteService.listAttractions(user.id)])
      .then(([list, favs]) => {
        setItems(list);
        setFavorites(favs);
      })
      .catch((err) => setNotice({ type: "error", text: getApiError(err) }));
  }, [setNotice, user.id]);

  useEffect(load, [load]);

  const favoriteIds = useMemo(() => new Set(favorites.map((item) => item.id)), [favorites]);
  const filtered = items.filter((item) => item.name.toLowerCase().includes(query.toLowerCase()));

  async function toggle(id?: number) {
    if (!id) return;
    try {
      const result = await favoriteService.toggleAttraction(user.id, id);
      setNotice({ type: "success", text: result.favorited ? "Adicionado aos favoritos." : "Removido dos favoritos." });
      load();
    } catch (err) {
      setNotice({ type: "error", text: getApiError(err) });
    }
  }

  return (
    <>
      <Header title="Pontos turísticos" subtitle="Todos os locais cadastrados." />
      <SearchBox value={query} onChange={setQuery} placeholder="Pesquisar por nome" />
      <div className="card-grid">
        {filtered.map((item) => (
          <AttractionCard key={item.id} attraction={item} favorite={favoriteIds.has(item.id)} onFavorite={() => toggle(item.id)} />
        ))}
      </div>
    </>
  );
}

function AttractionDetail({ id, user, setNotice }: { id: number; user: AuthResponse; setNotice: (notice: Notice) => void }) {
  const [attraction, setAttraction] = useState<TouristAttraction | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [average, setAverage] = useState(0);
  const [pois, setPois] = useState<PointOfInterest[]>([]);
  const [favorite, setFavorite] = useState(false);
  const [form, setForm] = useState({ rating: 5, comment: "", imageUrl: "" });

  const load = useCallback(() => {
    Promise.all([
      attractionService.get(id),
      reviewService.listByAttraction(id),
      reviewService.average(id),
      pointOfInterestService.listByAttraction(id),
      favoriteService.listAttractions(user.id)
    ])
      .then(([a, r, avg, p, favs]) => {
        setAttraction(a);
        setReviews(r);
        setAverage(avg.averageRating ?? 0);
        setPois(p);
        setFavorite(favs.some((item) => item.id === id));
      })
      .catch((err) => setNotice({ type: "error", text: getApiError(err) }));
  }, [id, setNotice, user.id]);

  useEffect(load, [load]);

  async function toggleFavorite() {
    const result = await favoriteService.toggleAttraction(user.id, id);
    setFavorite(result.favorited);
    setNotice({ type: "success", text: result.favorited ? "Adicionado aos favoritos." : "Removido dos favoritos." });
  }

  async function checkIn() {
    try {
      const result = await checkinService.perform({ touristId: user.id, attractionId: id, geolocation: await getLocationText() });
      setNotice({ type: "success", text: `Check-in realizado. +${result.pointsAwarded} pontos, nível ${result.level}.` });
    } catch (err) {
      setNotice({ type: "error", text: getApiError(err) });
    }
  }

  async function review(event: FormEvent) {
    event.preventDefault();
    try {
      await reviewService.upsert({
        touristId: user.id,
        attractionId: id,
        rating: Number(form.rating),
        comment: form.comment,
        imageUrl: form.imageUrl || undefined
      });
      setForm({ rating: 5, comment: "", imageUrl: "" });
      setNotice({ type: "success", text: "Avaliação publicada." });
      load();
    } catch (err) {
      setNotice({ type: "error", text: getApiError(err) });
    }
  }

  if (!attraction) return <Loading />;

  return (
    <>
      <button className="ghost-button" onClick={() => go("attractions")}>Voltar</button>
      <div className="gallery">
        {imageList(attraction).slice(0, 4).map((url) => <div className="gallery-item" key={url} style={{ backgroundImage: `url("${url}")` }} />)}
        {!imageList(attraction).length && <div className="gallery-item empty"><Camera /></div>}
      </div>
      <section className="detail-header">
        <div>
          <h1>{attraction.name}</h1>
          <p>{attraction.address}</p>
          <div className="meta-row">
            <span><Star size={15} /> {average.toFixed(1)}</span>
            {attraction.operatingHours && <span>{attraction.operatingHours}</span>}
            {attraction.contact && <span>{attraction.contact}</span>}
          </div>
        </div>
        <div className="actions">
          <IconButton active={favorite} label="Favoritar" icon={Heart} onClick={toggleFavorite} />
          <button className="primary-button compact-button" onClick={checkIn}><CheckCircle2 size={18} /> Check-in</button>
        </div>
      </section>
      <section className="two-columns">
        <div>
          <SectionTitle title="Descrição" />
          <p className="body-text">{attraction.description}</p>
          <SectionTitle title="Pontos de interesse" />
          <div className="list-grid">
            {pois.map((poi) => <InfoCard key={poi.id} title={poi.location} text={poi.description} icon={MapPin} />)}
            {!pois.length && <EmptyState icon={MapPin} title="Sem pontos de interesse" text="Nenhum item cadastrado para este local." />}
          </div>
        </div>
        <aside className="panel">
          <SectionTitle title="Avaliar" />
          <form className="form compact-form" onSubmit={review}>
            <label>Nota<input type="number" min="1" max="5" value={form.rating} onChange={(e) => setForm({ ...form, rating: Number(e.target.value) })} /></label>
            <label>Comentário<textarea value={form.comment} onChange={(e) => setForm({ ...form, comment: e.target.value })} required /></label>
            <label>URL da imagem<input value={form.imageUrl} onChange={(e) => setForm({ ...form, imageUrl: e.target.value })} /></label>
            <button className="primary-button">Publicar</button>
          </form>
          <SectionTitle title="Avaliações" />
          <div className="review-list">
            {reviews.map((item) => <ReviewItem key={item.id} review={item} />)}
          </div>
        </aside>
      </section>
    </>
  );
}

function TrailsPage({ user, setNotice }: { user: AuthResponse; setNotice: (notice: Notice) => void }) {
  const [items, setItems] = useState<Trail[]>([]);
  const [favorites, setFavorites] = useState<Trail[]>([]);

  const load = useCallback(() => {
    Promise.all([trailService.list(), favoriteService.listTrails(user.id)])
      .then(([trails, favs]) => {
        setItems(trails);
        setFavorites(favs);
      })
      .catch((err) => setNotice({ type: "error", text: getApiError(err) }));
  }, [setNotice, user.id]);

  useEffect(load, [load]);

  async function toggle(id?: number) {
    if (!id) return;
    const result = await favoriteService.toggleTrail(user.id, id);
    setNotice({ type: "success", text: result.favorited ? "Trilha favoritada." : "Trilha removida." });
    load();
  }

  const favoriteIds = new Set(favorites.map((trail) => trail.id));
  return (
    <>
      <Header title="Trilhas" subtitle="Categoria, dificuldade e tempo estimado." />
      <div className="list-grid">{items.map((trail) => <TrailCard key={trail.id} trail={trail} favorite={favoriteIds.has(trail.id)} onFavorite={() => toggle(trail.id)} />)}</div>
    </>
  );
}

function TrailDetail({ id, user, setNotice }: { id: number; user: AuthResponse; setNotice: (notice: Notice) => void }) {
  const [trail, setTrail] = useState<Trail | null>(null);
  const [attractions, setAttractions] = useState<TouristAttraction[]>([]);
  const [favorite, setFavorite] = useState(false);

  useEffect(() => {
    Promise.all([trailService.get(id), trailService.listAttractions(id), favoriteService.listTrails(user.id)])
      .then(([t, a, favs]) => {
        setTrail(t);
        setAttractions(a);
        setFavorite(favs.some((item) => item.id === id));
      })
      .catch((err) => setNotice({ type: "error", text: getApiError(err) }));
  }, [id, setNotice, user.id]);

  async function toggleFavorite() {
    const result = await favoriteService.toggleTrail(user.id, id);
    setFavorite(result.favorited);
    setNotice({ type: "success", text: result.favorited ? "Trilha favoritada." : "Trilha removida." });
  }

  if (!trail) return <Loading />;

  return (
    <>
      <button className="ghost-button" onClick={() => go("trails")}>Voltar</button>
      <section className="detail-header">
        <div>
          <h1>{trail.name}</h1>
          <div className="meta-row"><span>{trail.category}</span><span>{trail.difficulty}</span><span>{trail.estimatedTime}</span></div>
        </div>
        <IconButton active={favorite} label="Favoritar" icon={Heart} onClick={toggleFavorite} />
      </section>
      <p className="body-text">{trail.description}</p>
      <SectionTitle title="Pontos turísticos pertencentes" />
      <div className="card-grid">{attractions.map((item) => <AttractionCard key={item.id} attraction={item} />)}</div>
    </>
  );
}

function ItinerariesPage({ user, setNotice }: { user: AuthResponse; setNotice: (notice: Notice) => void }) {
  const [items, setItems] = useState<Itinerary[]>([]);
  const [trails, setTrails] = useState<Record<number, Trail[]>>({});

  useEffect(() => {
    itineraryService
      .listByTourist(user.id)
      .then(async (list) => {
        setItems(list);
        const pairs = await Promise.all(list.filter((item) => item.id).map(async (item) => [item.id!, await trailService.listByItinerary(item.id!)] as const));
        setTrails(Object.fromEntries(pairs));
      })
      .catch((err) => setNotice({ type: "error", text: getApiError(err) }));
  }, [setNotice, user.id]);

  return (
    <>
      <Header title="Roteiros" subtitle="Datas e trilhas relacionadas." />
      <div className="list-grid">
        {items.map((item) => (
          <article className="data-card" key={item.id}>
            <CalendarDays size={22} />
            <div>
              <strong>{item.name}</strong>
              <p>{formatDate(item.startDate)} até {formatDate(item.endDate)}</p>
              <div className="chip-row">{(trails[item.id ?? 0] ?? []).map((trail) => <span className="chip" key={trail.id}>{trail.name}</span>)}</div>
            </div>
          </article>
        ))}
        {!items.length && <EmptyState icon={CalendarDays} title="Sem roteiros" text="Os roteiros cadastrados pela API aparecerão aqui." />}
      </div>
    </>
  );
}

function ItineraryBuilderPage({ user, setNotice }: { user: AuthResponse; setNotice: (notice: Notice) => void }) {
  const [items, setItems] = useState<Itinerary[]>([]);
  const [trails, setTrails] = useState<Record<number, Trail[]>>({});
  const [attractions, setAttractions] = useState<TouristAttraction[]>([]);
  const [itineraryForm, setItineraryForm] = useState({ name: "", startDate: "", endDate: "" });
  const [trailForm, setTrailForm] = useState({
    name: "",
    description: "",
    category: "",
    difficulty: "",
    estimatedTime: "",
    rewardPoints: "",
    itineraryId: "",
    attractionIds: [] as number[]
  });

  const load = useCallback(() => {
    Promise.all([itineraryService.listByTourist(user.id), attractionService.list()])
      .then(async ([list, attractionList]) => {
        setItems(list);
        setAttractions(attractionList);
        setTrailForm((current) => ({
          ...current,
          itineraryId: current.itineraryId || String(list[0]?.id ?? "")
        }));
        const pairs = await Promise.all(
          list.filter((item) => item.id).map(async (item) => [item.id!, await trailService.listByItinerary(item.id!)] as const)
        );
        setTrails(Object.fromEntries(pairs));
      })
      .catch((err) => setNotice({ type: "error", text: getApiError(err) }));
  }, [setNotice, user.id]);

  useEffect(load, [load]);

  async function createItinerary(event: FormEvent) {
    event.preventDefault();
    try {
      const created = await itineraryService.create({
        name: itineraryForm.name,
        startDate: itineraryForm.startDate,
        endDate: itineraryForm.endDate,
        touristId: user.id
      });
      setItineraryForm({ name: "", startDate: "", endDate: "" });
      setTrailForm((current) => ({ ...current, itineraryId: String(created.id ?? current.itineraryId) }));
      setNotice({ type: "success", text: "Roteiro criado com sucesso." });
      load();
    } catch (err) {
      setNotice({ type: "error", text: getApiError(err) });
    }
  }

  async function createTrail(event: FormEvent) {
    event.preventDefault();

    if (!trailForm.itineraryId) {
      setNotice({ type: "error", text: "Crie ou selecione um roteiro para a trilha." });
      return;
    }

    if (!trailForm.attractionIds.length) {
      setNotice({ type: "error", text: "Selecione ao menos um ponto turistico para a trilha." });
      return;
    }

    try {
      const created = await trailService.create({
        name: trailForm.name,
        description: trailForm.description,
        category: trailForm.category,
        difficulty: trailForm.difficulty,
        estimatedTime: trailForm.estimatedTime,
        rewardPoints: trailForm.rewardPoints ? Number(trailForm.rewardPoints) : undefined,
        itineraryId: Number(trailForm.itineraryId)
      });

      if (created.id) {
        await Promise.all(trailForm.attractionIds.map((attractionId) => trailService.linkAttraction(created.id!, attractionId)));
      }

      setTrailForm({
        name: "",
        description: "",
        category: "",
        difficulty: "",
        estimatedTime: "",
        rewardPoints: "",
        itineraryId: trailForm.itineraryId,
        attractionIds: []
      });
      setNotice({ type: "success", text: "Trilha criada e vinculada aos pontos turisticos." });
      load();
    } catch (err) {
      setNotice({ type: "error", text: getApiError(err) });
    }
  }

  function toggleTrailAttraction(attractionId?: number) {
    if (!attractionId) return;
    setTrailForm((current) => ({
      ...current,
      attractionIds: current.attractionIds.includes(attractionId)
        ? current.attractionIds.filter((id) => id !== attractionId)
        : [...current.attractionIds, attractionId]
    }));
  }

  return (
    <>
      <Header title="Roteiros" subtitle="Crie roteiros e monte trilhas com pontos turisticos existentes." />
      <section className="planner-grid">
        <form className="form panel" onSubmit={createItinerary}>
          <SectionTitle title="Novo roteiro" />
          <label>
            Nome
            <input value={itineraryForm.name} onChange={(e) => setItineraryForm({ ...itineraryForm, name: e.target.value })} required />
          </label>
          <div className="form-row">
            <label>
              Inicio
              <input
                type="date"
                value={itineraryForm.startDate}
                onChange={(e) => setItineraryForm({ ...itineraryForm, startDate: e.target.value })}
                required
              />
            </label>
            <label>
              Fim
              <input
                type="date"
                value={itineraryForm.endDate}
                onChange={(e) => setItineraryForm({ ...itineraryForm, endDate: e.target.value })}
                required
              />
            </label>
          </div>
          <button className="primary-button">Criar roteiro</button>
        </form>

        <form className="form panel" onSubmit={createTrail}>
          <SectionTitle title="Nova trilha" />
          <label>
            Roteiro
            <select value={trailForm.itineraryId} onChange={(e) => setTrailForm({ ...trailForm, itineraryId: e.target.value })} required>
              <option value="">Selecione um roteiro</option>
              {items.map((item) => (
                <option key={item.id} value={item.id}>
                  {item.name}
                </option>
              ))}
            </select>
          </label>
          <div className="form-row">
            <label>
              Nome
              <input value={trailForm.name} onChange={(e) => setTrailForm({ ...trailForm, name: e.target.value })} required />
            </label>
            <label>
              Categoria
              <input value={trailForm.category} onChange={(e) => setTrailForm({ ...trailForm, category: e.target.value })} required />
            </label>
          </div>
          <label>
            Descricao
            <textarea value={trailForm.description} onChange={(e) => setTrailForm({ ...trailForm, description: e.target.value })} required />
          </label>
          <div className="form-row">
            <label>
              Dificuldade
              <input value={trailForm.difficulty} onChange={(e) => setTrailForm({ ...trailForm, difficulty: e.target.value })} required />
            </label>
            <label>
              Tempo estimado
              <input value={trailForm.estimatedTime} onChange={(e) => setTrailForm({ ...trailForm, estimatedTime: e.target.value })} required />
            </label>
            <label>
              Pontos
              <input
                type="number"
                min="0"
                value={trailForm.rewardPoints}
                onChange={(e) => setTrailForm({ ...trailForm, rewardPoints: e.target.value })}
              />
            </label>
          </div>
          <div>
            <span className="field-label">Pontos turisticos da trilha</span>
            <div className="checkbox-list">
              {attractions.map((attraction) => (
                <label className="checkbox-card" key={attraction.id}>
                  <input
                    type="checkbox"
                    checked={trailForm.attractionIds.includes(attraction.id ?? 0)}
                    onChange={() => toggleTrailAttraction(attraction.id)}
                  />
                  <span>
                    <strong>{attraction.name}</strong>
                    <small>{attraction.address}</small>
                  </span>
                </label>
              ))}
              {!attractions.length && <p className="muted">Nenhum ponto turistico cadastrado na API.</p>}
            </div>
          </div>
          <button className="primary-button">Criar trilha</button>
        </form>
      </section>

      <SectionTitle title="Meus roteiros" />
      <div className="list-grid">
        {items.map((item) => (
          <article className="data-card" key={item.id}>
            <CalendarDays size={22} />
            <div className="grow">
              <strong>{item.name}</strong>
              <p>{formatDate(item.startDate)} ate {formatDate(item.endDate)}</p>
              <div className="chip-row">
                {(trails[item.id ?? 0] ?? []).map((trail) => (
                  <button className="chip chip-button" key={trail.id} onClick={() => go(`trail/${trail.id}`)}>
                    {trail.name}
                  </button>
                ))}
              </div>
            </div>
          </article>
        ))}
        {!items.length && <EmptyState icon={CalendarDays} title="Sem roteiros" text="Crie seu primeiro roteiro usando o formulario acima." />}
      </div>
    </>
  );
}

function FavoritesPage({ user, setNotice }: { user: AuthResponse; setNotice: (notice: Notice) => void }) {
  const [attractions, setAttractions] = useState<TouristAttraction[]>([]);
  const [trails, setTrails] = useState<Trail[]>([]);

  const load = useCallback(() => {
    Promise.all([favoriteService.listAttractions(user.id), favoriteService.listTrails(user.id)])
      .then(([a, t]) => {
        setAttractions(a);
        setTrails(t);
      })
      .catch((err) => setNotice({ type: "error", text: getApiError(err) }));
  }, [setNotice, user.id]);

  useEffect(load, [load]);

  async function removeAttraction(id?: number) {
    if (!id) return;
    await favoriteService.toggleAttraction(user.id, id);
    load();
  }

  async function removeTrail(id?: number) {
    if (!id) return;
    await favoriteService.toggleTrail(user.id, id);
    load();
  }

  return (
    <>
      <Header title="Favoritos" subtitle="Pontos turísticos e trilhas salvos." />
      <SectionTitle title="Pontos turísticos" />
      <div className="card-grid">{attractions.map((item) => <AttractionCard key={item.id} attraction={item} favorite onFavorite={() => removeAttraction(item.id)} />)}</div>
      <SectionTitle title="Trilhas" />
      <div className="list-grid">{trails.map((trail) => <TrailCard key={trail.id} trail={trail} favorite onFavorite={() => removeTrail(trail.id)} />)}</div>
      {!attractions.length && !trails.length && <EmptyState icon={Heart} title="Sem favoritos" text="Explore novos locais para criar sua lista." />}
    </>
  );
}

function ProfilePage({ user, setNotice }: { user: AuthResponse; setNotice: (notice: Notice) => void }) {
  const [profile, setProfile] = useState<Tourist | null>(null);
  const [checkins, setCheckins] = useState<CheckIn[]>([]);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [achievements, setAchievements] = useState<Achievement[]>([]);

  useEffect(() => {
    Promise.all([userService.getTourist(user.id), checkinService.listByTourist(user.id), reviewService.listByTourist(user.id), achievementService.listByTourist(user.id)])
      .then(([p, c, r, a]) => {
        setProfile(p);
        setCheckins(c);
        setReviews(r);
        setAchievements(a);
      })
      .catch((err) => setNotice({ type: "error", text: getApiError(err) }));
  }, [setNotice, user.id]);

  if (!profile) return <Loading />;

  return (
    <>
      <Header title="Perfil" subtitle="Pontuação, nível e histórico." />
      <section className="profile-grid">
        <article className="profile-card">
          <div className="avatar">{profile.name.charAt(0).toUpperCase()}</div>
          <h2>{profile.name}</h2>
          <p>{profile.email}</p>
          <div className="stats">
            <Stat label="Pontos" value={profile.totalPoints ?? 0} />
            <Stat label="Nível" value={profile.level ?? 1} />
            <Stat label="Check-ins" value={checkins.length} />
            <Stat label="Conquistas" value={achievements.length} />
          </div>
        </article>
      </section>
      <section className="two-columns">
        <div>
          <SectionTitle title="Últimos check-ins" />
          <div className="list-grid">{checkins.slice(-5).reverse().map((item) => <InfoCard key={item.id} title={`Ponto #${item.attractionId}`} text={formatDateTime(item.dateTime)} icon={CheckCircle2} />)}</div>
        </div>
        <div>
          <SectionTitle title="Últimas avaliações" />
          <div className="review-list">{reviews.slice(-5).reverse().map((item) => <ReviewItem key={item.id} review={item} />)}</div>
        </div>
      </section>
    </>
  );
}

function AchievementsPage({ user, setNotice }: { user: AuthResponse; setNotice: (notice: Notice) => void }) {
  const [all, setAll] = useState<Achievement[]>([]);
  const [mine, setMine] = useState<Achievement[]>([]);

  useEffect(() => {
    Promise.all([achievementService.list(), achievementService.listByTourist(user.id)])
      .then(([a, m]) => {
        setAll(a);
        setMine(m);
      })
      .catch((err) => setNotice({ type: "error", text: getApiError(err) }));
  }, [setNotice, user.id]);

  const unlocked = new Set(mine.map((item) => item.id));
  return (
    <>
      <Header title="Conquistas" subtitle="Marcos desbloqueados pela sua exploração." />
      <div className="achievement-grid">
        {all.map((item) => (
          <article className={`achievement-card ${unlocked.has(item.id) ? "unlocked" : ""}`} key={item.id}>
            <div className="achievement-icon">{item.icon || "★"}</div>
            <strong>{item.name}</strong>
            <p>{item.description}</p>
          </article>
        ))}
      </div>
    </>
  );
}

function InstitutionAdmin({ user, setNotice }: { user: AuthResponse; setNotice: (notice: Notice) => void }) {
  const [tab, setTab] = useState("attractions");
  return (
    <>
      <Header title="Painel da instituição" subtitle="Gerencie conteúdo e acompanhe check-ins." />
      <div className="tabs">
        <button className={tab === "attractions" ? "active" : ""} onClick={() => setTab("attractions")}>Pontos</button>
        <button className={tab === "trails" ? "active" : ""} onClick={() => setTab("trails")}>Trilhas</button>
        <button className={tab === "itineraries" ? "active" : ""} onClick={() => setTab("itineraries")}>Roteiros</button>
        <button className={tab === "pois" ? "active" : ""} onClick={() => setTab("pois")}>Interesses</button>
        <button className={tab === "checkins" ? "active" : ""} onClick={() => setTab("checkins")}>Check-ins</button>
      </div>
      {tab === "attractions" && <AttractionAdmin user={user} setNotice={setNotice} />}
      {tab === "trails" && <SimpleTrailTable setNotice={setNotice} />}
      {tab === "itineraries" && <SimpleItineraryTable setNotice={setNotice} />}
      {tab === "pois" && <PoiAdmin user={user} setNotice={setNotice} />}
      {tab === "checkins" && <CheckinAdmin user={user} setNotice={setNotice} />}
    </>
  );
}

function AttractionAdmin({ user, setNotice }: { user: AuthResponse; setNotice: (notice: Notice) => void }) {
  const [items, setItems] = useState<TouristAttraction[]>([]);
  const [form, setForm] = useState<TouristAttraction>({ ...emptyAttraction, institutionId: user.id });
  const [editingId, setEditingId] = useState<number | null>(null);

  const load = useCallback(() => {
    attractionService.listByInstitution(user.id).then(setItems).catch((err) => setNotice({ type: "error", text: getApiError(err) }));
  }, [setNotice, user.id]);

  useEffect(load, [load]);

  async function submit(event: FormEvent) {
    event.preventDefault();
    try {
      const payload = { ...form, institutionId: user.id };
      if (editingId) await attractionService.update(editingId, payload);
      else await attractionService.create(payload);
      setForm({ ...emptyAttraction, institutionId: user.id });
      setEditingId(null);
      setNotice({ type: "success", text: editingId ? "Ponto atualizado." : "Ponto cadastrado." });
      load();
    } catch (err) {
      setNotice({ type: "error", text: getApiError(err) });
    }
  }

  return (
    <section className="admin-grid">
      <form className="form admin-form" onSubmit={submit}>
        <SectionTitle title={editingId ? "Editar ponto turístico" : "Cadastrar ponto turístico"} />
        <label>Nome<input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required /></label>
        <label>Endereço<input value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} required /></label>
        <label>Descrição<textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} required /></label>
        <label>Contato<input value={form.contact ?? ""} onChange={(e) => setForm({ ...form, contact: e.target.value })} /></label>
        <label>Horário<input value={form.operatingHours ?? ""} onChange={(e) => setForm({ ...form, operatingHours: e.target.value })} /></label>
        <label>Imagem principal<input value={form.profileImage ?? ""} onChange={(e) => setForm({ ...form, profileImage: e.target.value })} /></label>
        <label>Galeria<input value={form.gallery ?? ""} onChange={(e) => setForm({ ...form, gallery: e.target.value })} /></label>
        <button className="primary-button">{editingId ? "Salvar" : "Cadastrar"}</button>
      </form>
      <DataTable
        columns={["Nome", "Endereço", "Status", "Ações"]}
        rows={items.map((item) => [
          item.name,
          item.address,
          item.status ?? "",
          <button className="small-button" onClick={() => { setEditingId(item.id ?? null); setForm(item); }}>Editar</button>
        ])}
      />
    </section>
  );
}

function PoiAdmin({ user, setNotice }: { user: AuthResponse; setNotice: (notice: Notice) => void }) {
  const [attractions, setAttractions] = useState<TouristAttraction[]>([]);
  const [items, setItems] = useState<PointOfInterest[]>([]);
  const [form, setForm] = useState<PointOfInterest>({ location: "", description: "" });

  function loadPoi(attractionId?: number) {
    if (!attractionId) return setItems([]);
    pointOfInterestService.listByAttraction(attractionId).then(setItems).catch((err) => setNotice({ type: "error", text: getApiError(err) }));
  }

  useEffect(() => {
    attractionService.listByInstitution(user.id).then((list) => {
      setAttractions(list);
      if (list[0]?.id) {
        setForm((current) => ({ ...current, attractionId: list[0].id }));
        loadPoi(list[0].id);
      }
    });
  }, [user.id]);

  async function submit(event: FormEvent) {
    event.preventDefault();
    try {
      await pointOfInterestService.create(form);
      setNotice({ type: "success", text: "Ponto de interesse cadastrado." });
      setForm({ location: "", description: "", attractionId: form.attractionId });
      loadPoi(form.attractionId);
    } catch (err) {
      setNotice({ type: "error", text: getApiError(err) });
    }
  }

  return (
    <section className="admin-grid">
      <form className="form admin-form" onSubmit={submit}>
        <SectionTitle title="Cadastrar ponto de interesse" />
        <label>
          Ponto turístico
          <select value={form.attractionId ?? ""} onChange={(e) => { const id = Number(e.target.value); setForm({ ...form, attractionId: id }); loadPoi(id); }} required>
            {attractions.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}
          </select>
        </label>
        <label>Localização<input value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} required /></label>
        <label>Descrição<textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} required /></label>
        <button className="primary-button">Cadastrar</button>
      </form>
      <DataTable columns={["Localização", "Descrição"]} rows={items.map((item) => [item.location, item.description])} />
    </section>
  );
}

function CheckinAdmin({ user, setNotice }: { user: AuthResponse; setNotice: (notice: Notice) => void }) {
  const [items, setItems] = useState<CheckIn[]>([]);
  useEffect(() => {
    attractionService
      .listByInstitution(user.id)
      .then((attractions) => Promise.all(attractions.filter((item) => item.id).map((item) => checkinService.listByAttraction(item.id!))))
      .then((groups) => setItems(groups.flat()))
      .catch((err) => setNotice({ type: "error", text: getApiError(err) }));
  }, [setNotice, user.id]);

  return <DataTable columns={["Turista", "Ponto", "Data", "Geolocalização"]} rows={items.map((item) => [`#${item.touristId}`, `#${item.attractionId}`, formatDateTime(item.dateTime), item.geolocation ?? ""])} />;
}

function SimpleTrailTable({ setNotice }: { setNotice: (notice: Notice) => void }) {
  const [items, setItems] = useState<Trail[]>([]);
  useEffect(() => {
    trailService.list().then(setItems).catch((err) => setNotice({ type: "error", text: getApiError(err) }));
  }, [setNotice]);
  return <DataTable columns={["Nome", "Categoria", "Dificuldade", "Tempo"]} rows={items.map((item) => [item.name, item.category, item.difficulty, item.estimatedTime])} />;
}

function SimpleItineraryTable({ setNotice }: { setNotice: (notice: Notice) => void }) {
  const [items, setItems] = useState<Itinerary[]>([]);
  useEffect(() => {
    itineraryService.list().then(setItems).catch((err) => setNotice({ type: "error", text: getApiError(err) }));
  }, [setNotice]);
  return <DataTable columns={["Nome", "Início", "Fim", "Turista"]} rows={items.map((item) => [item.name, formatDate(item.startDate), formatDate(item.endDate), `#${item.touristId}`])} />;
}

function AttractionCard({ attraction, favorite, onFavorite }: { attraction: TouristAttraction; favorite?: boolean; onFavorite?: () => void }) {
  const image = mainImage(attraction);
  return (
    <article className="attraction-card">
      <div className="card-image" style={image ? { backgroundImage: `url("${image}")` } : undefined}>{!image && <Camera size={26} />}</div>
      <div className="card-body">
        <div className="card-title">
          <h3>{attraction.name}</h3>
          {onFavorite && <IconButton active={favorite} label="Favoritar" icon={Heart} onClick={onFavorite} />}
        </div>
        <p className="muted">{attraction.address}</p>
        <p>{crop(attraction.description)}</p>
        <button className="link-button" onClick={() => go(`attraction/${attraction.id}`)}>Ver detalhes <ChevronRight size={16} /></button>
      </div>
    </article>
  );
}

function MiniAttraction({ attraction }: { attraction: TouristAttraction }) {
  return (
    <article className="data-card clickable" onClick={() => go(`attraction/${attraction.id}`)}>
      <MapPin size={22} />
      <div><strong>{attraction.name}</strong><p>{attraction.address}</p></div>
      <ChevronRight size={18} />
    </article>
  );
}

function TrailCard({ trail, favorite, onFavorite }: { trail: Trail; favorite?: boolean; onFavorite?: () => void }) {
  return (
    <article className="data-card">
      <Route size={22} />
      <div className="grow">
        <strong>{trail.name}</strong>
        <p>{crop(trail.description, 90)}</p>
        <div className="chip-row"><span className="chip">{trail.category}</span><span className="chip">{trail.difficulty}</span><span className="chip">{trail.estimatedTime}</span></div>
      </div>
      <div className="row-actions">
        {onFavorite && <IconButton active={favorite} label="Favoritar" icon={Heart} onClick={onFavorite} />}
        <button className="icon-button" aria-label="Ver trilha" onClick={() => go(`trail/${trail.id}`)}><ChevronRight size={18} /></button>
      </div>
    </article>
  );
}

function Header({ title, subtitle }: { title: string; subtitle: string }) {
  return (
    <header className="page-header">
      <span>TurisGO</span>
      <h1>{title}</h1>
      <p>{subtitle}</p>
    </header>
  );
}

function SectionTitle({ title }: { title: string }) {
  return <h2 className="section-title">{title}</h2>;
}

function SearchBox({ value, onChange, placeholder }: { value: string; onChange: (value: string) => void; placeholder: string }) {
  return (
    <label className="search-box">
      <Search size={20} />
      <input value={value} placeholder={placeholder} onChange={(e) => onChange(e.target.value)} />
    </label>
  );
}

function IconButton({ active, label, icon: Icon, onClick }: { active?: boolean; label: string; icon: typeof Home; onClick: () => void }) {
  return (
    <button className={`icon-button ${active ? "active" : ""}`} aria-label={label} title={label} onClick={onClick}>
      <Icon size={18} />
    </button>
  );
}

function InfoCard({ title, text, icon: Icon }: { title: string; text: string; icon: typeof Home }) {
  return (
    <article className="data-card">
      <Icon size={22} />
      <div><strong>{title}</strong><p>{text}</p></div>
    </article>
  );
}

function EmptyState({ icon: Icon, title, text }: { icon: typeof Home; title: string; text: string }) {
  return (
    <div className="empty-state">
      <Icon size={28} />
      <strong>{title}</strong>
      <p>{text}</p>
    </div>
  );
}

function Stat({ label, value }: { label: string; value: number }) {
  return <div className="stat"><strong>{value}</strong><span>{label}</span></div>;
}

function ReviewItem({ review }: { review: Review }) {
  return (
    <article className="review-item">
      <strong>{review.rating}/5</strong>
      <p>{review.comment}</p>
      <span>{formatDate(review.reviewDate)}</span>
    </article>
  );
}

function NoticeBar({ notice, onClose }: { notice: NonNullable<Notice>; onClose: () => void }) {
  return (
    <div className={`notice ${notice.type}`}>
      <span>{notice.text}</span>
      <button onClick={onClose}>OK</button>
    </div>
  );
}

function Loading() {
  return <div className="loading">Carregando...</div>;
}

function DataTable({ columns, rows }: { columns: string[]; rows: React.ReactNode[][] }) {
  return (
    <div className="table-wrap">
      <table>
        <thead><tr>{columns.map((column) => <th key={column}>{column}</th>)}</tr></thead>
        <tbody>
          {rows.map((row, rowIndex) => (
            <tr key={rowIndex}>{row.map((cell, cellIndex) => <td key={cellIndex}>{cell}</td>)}</tr>
          ))}
        </tbody>
      </table>
      {!rows.length && <EmptyState icon={TableProperties} title="Sem registros" text="Nenhum item encontrado." />}
    </div>
  );
}

function getLocationText() {
  if (!navigator.geolocation) return Promise.resolve("Não informado");
  return new Promise<string>((resolve) => {
    navigator.geolocation.getCurrentPosition(
      (position) => resolve(`${position.coords.latitude},${position.coords.longitude}`),
      () => resolve("Não informado"),
      { timeout: 5000, enableHighAccuracy: true }
    );
  });
}
