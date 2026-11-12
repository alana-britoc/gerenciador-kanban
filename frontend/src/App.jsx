import { useEffect, useState } from 'react'
import * as api from './api/cartoes'
import Cartao from './components/Cartao'
import NovoCartao from './components/NovoCartao'
import './App.css'

const COLUNAS = [
  { id: 'A_FAZER', nome: 'A fazer' },
  { id: 'FAZENDO', nome: 'Fazendo' },
  { id: 'FEITO', nome: 'Feito' },
]

export default function App() {
  const [cartoes, setCartoes] = useState([])
  const [erro, setErro] = useState(null)

  const carregar = () =>
    api.listar().then(setCartoes).then(() => setErro(null)).catch(() => setErro('Back fora do ar?'))

  useEffect(() => { carregar() }, [])

  const acao = (fn) => (...args) => fn(...args).then(carregar).catch((e) => setErro(e.message))

  return (
    <main>
      <h1>Gerenciador Kanban</h1>
      <NovoCartao onCriar={acao(api.criar)} />
      {erro && <p className="erro">{erro}</p>}
      <div className="quadro">
        {COLUNAS.map((col) => (
          <section key={col.id} className="coluna">
            <h2>{col.nome}</h2>
            {cartoes.filter((c) => c.coluna === col.id).map((c) => (
              <Cartao key={c.id} cartao={c} onMover={acao(api.mover)} onRemover={acao(api.remover)} />
            ))}
          </section>
        ))}
      </div>
    </main>
  )
}
