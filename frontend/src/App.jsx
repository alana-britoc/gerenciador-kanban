import { useEffect, useRef, useState } from 'react'
import { Bell, CalendarDays, CircleAlert, Columns3, SquareKanban, X } from 'lucide-react'
import * as atividade from './api/atividade'
import * as api from './api/cartoes'
import { escutarQuadro } from './api/tempoReal'
import Avisos from './components/Avisos'
import Calendario from './components/Calendario'
import DetalhesCartao from './components/DetalhesCartao'
import PainelAtividade from './components/PainelAtividade'
import Quadro from './components/Quadro'
import './App.css'

const VISOES = [
  { id: 'quadro', nome: 'Quadro', Icone: Columns3, descricao: 'Arraste os cartões entre as colunas ou use o menu ⋯ de cada um' },
  { id: 'calendario', nome: 'Calendário', Icone: CalendarDays, descricao: 'Os cartões no dia do seu prazo' },
]

const CHAVE_VISAO = 'kanban.visao'

const TELA_LARGA = '(min-width: 1100px)'
const DURACAO_AVISO_MS = 6000

function visaoSalva() {
  try {
    const salva = localStorage.getItem(CHAVE_VISAO)
    return VISOES.some((v) => v.id === salva) ? salva : 'quadro'
  } catch {
    return 'quadro'
  }
}

function telaLarga() {
  return window.matchMedia?.(TELA_LARGA).matches ?? true
}

export default function App() {
  const [cartoes, setCartoes] = useState([])
  const [erro, setErro] = useState(null)
  const [visao, setVisao] = useState(visaoSalva)
  const [abertoId, setAbertoId] = useState(null)
  const [historico, setHistorico] = useState([])
  const [notificacoes, setNotificacoes] = useState([])
  const [painelAberto, setPainelAberto] = useState(telaLarga)
  const [lidas, setLidas] = useState(0)
  const [avisos, setAvisos] = useState([])
  const [festa, setFesta] = useState(0)
  const notificacoesConhecidas = useRef(null)
  const painelAbertoAgora = useRef(painelAberto)
  const proximoAviso = useRef(1)
  const visaoAtual = VISOES.find((v) => v.id === visao)
  const cartaoAberto = cartoes.find((c) => c.id === abertoId)
  const naoLidas = painelAberto ? 0 : Math.max(0, notificacoes.length - lidas)

  useEffect(() => {
    painelAbertoAgora.current = painelAberto
  }, [painelAberto])

  useEffect(() => {
    if (!painelAberto) return
    const apertouEsc = (e) => {
      if (e.key === 'Escape' && !telaLarga() && !document.querySelector('dialog[open]')) setPainelAberto(false)
    }
    window.addEventListener('keydown', apertouEsc)
    return () => window.removeEventListener('keydown', apertouEsc)
  }, [painelAberto])

  function alternarPainel() {
    if (!painelAberto) setLidas(notificacoes.length)
    setPainelAberto(!painelAberto)
  }

  function fecharAviso(id) {
    setAvisos((atuais) => atuais.filter((a) => a.id !== id))
  }

  function mostrarAviso(texto) {
    const id = proximoAviso.current++
    setAvisos((atuais) => [...atuais, { id, texto }])
    setTimeout(() => fecharAviso(id), DURACAO_AVISO_MS)
  }

  function registrarNotificacoes(lista) {
    const anteriores = notificacoesConhecidas.current
    notificacoesConhecidas.current = lista.length
    if (anteriores === null || lista.length < anteriores) {
      setLidas(lista.length)
      return
    }
    if (painelAbertoAgora.current) setLidas(lista.length)
    const novas = lista.slice(anteriores)
    if (novas.length === 0) return
    setFesta((f) => f + 1)
    novas.forEach((n) => mostrarAviso(n.mensagem))
  }

  function trocarVisao(id) {
    setVisao(id)
    try {
      localStorage.setItem(CHAVE_VISAO, id)
    } catch {}
  }

  const carregar = () =>
    Promise.all([api.listar(), atividade.listarHistorico(), atividade.listarNotificacoes()])
      .then(([lista, movimentos, concluidos]) => {
        setCartoes(lista)
        setHistorico(movimentos)
        setNotificacoes(concluidos)
        registrarNotificacoes(concluidos)
        setErro(null)
      })
      .catch(() => setErro('Back fora do ar?'))

  useEffect(() => { carregar() }, [])

  // quando um cartão muda de coluna em outra aba, recarrega o quadro
  useEffect(() => escutarQuadro(() => carregar()), [])

  const acao = (fn) => (...args) =>
    fn(...args)
      .then(carregar)
      .then(() => true)
      .catch((e) => {
        setErro(e.message)
        return false
      })

  async function mover(id, coluna) {
    setCartoes((atuais) => atuais.map((c) => (c.id === id ? { ...c, coluna } : c)))
    const ok = await acao(api.mover)(id, coluna)
    if (!ok) api.listar().then(setCartoes).catch(() => {})
    return ok
  }

  return (
    <>
      <div className="fundo" aria-hidden="true">
        <span />
        <span />
        <span />
      </div>

      <header className="topo vidro">
        <div className="topo-conteudo">
          <div className="marca">
            <span className="marca-icone"><SquareKanban size={18} /></span>
            <div>
              <strong>Gerenciador Kanban</strong>
              <small>Padrões de Projeto</small>
            </div>
          </div>
          <div className="topo-acoes">
            <span className="topo-resumo">
              {cartoes.length} {cartoes.length === 1 ? 'cartão' : 'cartões'}
            </span>
            <button
              type="button"
              className="botao-sino"
              aria-label={naoLidas > 0 ? `Atividade, ${naoLidas} novas` : 'Atividade'}
              aria-expanded={painelAberto}
              aria-controls="painel-atividade"
              onClick={alternarPainel}
            >
              <Bell size={19} strokeWidth={1.8} />
              {naoLidas > 0 && <span className="sino-contador">{naoLidas}</span>}
            </button>
          </div>
        </div>
      </header>

      <main className="pagina">
        <div className="pagina-titulo">
          <div>
            <h1>{visaoAtual.nome}</h1>
            <p>{visaoAtual.descricao}</p>
          </div>
          <div role="tablist" aria-label="Forma de visualizar" className="seletor seletor-visao">
            {VISOES.map(({ id, nome, Icone }) => (
              <button
                key={id}
                type="button"
                role="tab"
                aria-selected={visao === id}
                onClick={() => trocarVisao(id)}
              >
                <Icone size={14} /> {nome}
              </button>
            ))}
          </div>
        </div>

        {erro && (
          <div className="aviso-erro" role="alert">
            <CircleAlert size={16} />
            <span>{erro}</span>
            <button type="button" aria-label="Fechar aviso" onClick={() => setErro(null)}>
              <X size={14} />
            </button>
          </div>
        )}

        <div className={`area${painelAberto ? ' com-painel' : ''}`}>
          <div className="area-conteudo">
            {visao === 'calendario' ? (
              <Calendario cartoes={cartoes} onAbrir={setAbertoId} />
            ) : (
              <Quadro
                cartoes={cartoes}
                festa={festa}
                onAbrir={setAbertoId}
                onCriar={acao(api.criar)}
                onMover={mover}
                onRemover={acao(api.remover)}
                onAdicionarExtra={acao(api.adicionarExtra)}
              />
            )}
          </div>

          {painelAberto && (
            <>
              <div className="painel-fundo" aria-hidden="true" onClick={() => setPainelAberto(false)} />
              <PainelAtividade
                historico={historico}
                notificacoes={notificacoes}
                existe={(id) => cartoes.some((c) => c.id === id)}
                onAbrir={setAbertoId}
                onFechar={() => setPainelAberto(false)}
              />
            </>
          )}
        </div>

        {cartaoAberto && (
          <DetalhesCartao
            key={cartaoAberto.id}
            cartao={cartaoAberto}
            onFechar={() => setAbertoId(null)}
            onSalvar={acao(api.editar)}
            onMover={mover}
            onAdicionarExtra={acao(api.adicionarExtra)}
            onRemoverExtra={acao(api.removerExtra)}
            onRemover={acao(api.remover)}
          />
        )}
      </main>

      <Avisos avisos={avisos} onFechar={fecharAviso} />
    </>
  )
}
