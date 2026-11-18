import { useEffect, useState } from 'react'
import { CircleAlert, SquareKanban, X } from 'lucide-react'
import * as api from './api/cartoes'
import Quadro from './components/Quadro'
import './App.css'

export default function App() {
  const [cartoes, setCartoes] = useState([])
  const [erro, setErro] = useState(null)

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
          <h1>Quadro</h1>
          <p>Arraste os cartões entre as colunas ou use o menu ⋯ de cada um</p>
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

        <Quadro
          cartoes={cartoes}
          onCriar={acao(api.criar)}
          onMover={mover}
          onRemover={acao(api.remover)}
          onAdicionarExtra={acao(api.adicionarExtra)}
        />
      </main>
    </>
  )
}
