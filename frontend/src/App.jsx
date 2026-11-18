import { useEffect, useState } from 'react'
import { CircleAlert, Plus, SquareKanban, X } from 'lucide-react'
import * as api from './api/cartoes'
import { COLUNAS } from './colunas'
import Cartao from './components/Cartao'
import NovoCartao from './components/NovoCartao'
import './App.css'

export default function App() {
  const [cartoes, setCartoes] = useState([])
  const [erro, setErro] = useState(null)
  const [criando, setCriando] = useState(false)

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
          <p>Tarefas, bugs e melhorias do projeto</p>
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

        <div className="quadro">
          {COLUNAS.map((col) => {
            const daColuna = cartoes.filter((c) => c.coluna === col.id)
            const comFormulario = col.id === 'A_FAZER' && criando
            return (
              <section key={col.id} className="coluna vidro" aria-label={col.nome} style={{ '--cor-coluna': col.cor }}>
                <header className="coluna-topo">
                  <span className="ponto" />
                  <h2>{col.nome}</h2>
                  <span className="contador">{daColuna.length}</span>
                  {col.id === 'A_FAZER' && (
                    <button
                      type="button"
                      className="botao-adicionar"
                      aria-label={criando ? 'Fechar novo cartão' : 'Novo cartão'}
                      aria-expanded={criando}
                      onClick={() => setCriando((v) => !v)}
                    >
                      <Plus size={14} />
                    </button>
                  )}
                </header>

                <div className="coluna-lista">
                  {comFormulario && <NovoCartao onCriar={acao(api.criar)} onFechar={() => setCriando(false)} />}
                  {daColuna.map((c) => (
                    <Cartao
                      key={c.id}
                      cartao={c}
                      onMover={acao(api.mover)}
                      onRemover={acao(api.remover)}
                      onAdicionarExtra={acao(api.adicionarExtra)}
                    />
                  ))}
                  {daColuna.length === 0 && !comFormulario && (
                    <p className="coluna-vazia">
                      {col.id === 'A_FAZER' ? 'Nenhum cartão. Use o + para criar.' : 'Nenhum cartão aqui'}
                    </p>
                  )}
                </div>
              </section>
            )
          })}
        </div>
      </main>
    </>
  )
}
