import { useEffect, useState } from 'react'
import { CalendarDays, CircleAlert, Columns3, SquareKanban, X } from 'lucide-react'
import * as api from './api/cartoes'
import Calendario from './components/Calendario'
import Quadro from './components/Quadro'
import './App.css'

const VISOES = [
  { id: 'quadro', nome: 'Quadro', Icone: Columns3, descricao: 'Arraste os cartões entre as colunas ou use o menu ⋯ de cada um' },
  { id: 'calendario', nome: 'Calendário', Icone: CalendarDays, descricao: 'Os cartões no dia do seu prazo' },
]

const CHAVE_VISAO = 'kanban.visao'

function visaoSalva() {
  try {
    const salva = localStorage.getItem(CHAVE_VISAO)
    return VISOES.some((v) => v.id === salva) ? salva : 'quadro'
  } catch {
    return 'quadro'
  }
}

export default function App() {
  const [cartoes, setCartoes] = useState([])
  const [erro, setErro] = useState(null)
  const [visao, setVisao] = useState(visaoSalva)
  const visaoAtual = VISOES.find((v) => v.id === visao)

  function trocarVisao(id) {
    setVisao(id)
    try {
      localStorage.setItem(CHAVE_VISAO, id)
    } catch {}
  }

  const carregar = () =>
    api.listar().then(setCartoes).then(() => setErro(null)).catch(() => setErro('Back fora do ar?'))

  useEffect(() => { carregar() }, [])

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
          <span className="topo-resumo">
            {cartoes.length} {cartoes.length === 1 ? 'cartão' : 'cartões'}
          </span>
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

        {visao === 'calendario' ? (
          <Calendario cartoes={cartoes} />
        ) : (
          <Quadro
            cartoes={cartoes}
            onCriar={acao(api.criar)}
            onMover={mover}
            onRemover={acao(api.remover)}
            onAdicionarExtra={acao(api.adicionarExtra)}
          />
        )}
      </main>
    </>
  )
}
