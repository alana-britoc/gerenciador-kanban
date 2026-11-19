import { useDroppable } from '@dnd-kit/core'
import { Plus } from 'lucide-react'

const CORES_FAISCAS = ['#3b5bdb', '#7cc4f0', '#3fa87a', '#f2a33a', '#8ba5ff']
const QUANTIDADE_FAISCAS = 14

function Faiscas() {
  return (
    <span className="faiscas" aria-hidden="true">
      {Array.from({ length: QUANTIDADE_FAISCAS }, (_, i) => {
        const angulo = (i / QUANTIDADE_FAISCAS) * Math.PI * 2
        return (
          <span
            key={i}
            style={{
              '--x': `${Math.round(Math.cos(angulo) * 100)}px`,
              '--y': `${Math.round(Math.sin(angulo) * 55 + 24)}px`,
              '--cor': CORES_FAISCAS[i % CORES_FAISCAS.length],
            }}
          />
        )
      })}
    </span>
  )
}

export default function Coluna({ coluna, quantidade, arrastado, criando, onAlternarCriacao, formulario, festa = 0, children }) {
  const { setNodeRef, isOver } = useDroppable({ id: coluna.id })
  const vemDeOutraColuna = arrastado && arrastado.coluna !== coluna.id
  const alvo = isOver && vemDeOutraColuna
  const podeCriar = coluna.id === 'A_FAZER'

  return (
    <section
      ref={setNodeRef}
      className={`coluna vidro${alvo ? ' coluna-alvo' : ''}`}
      aria-label={coluna.nome}
      style={{ '--cor-coluna': coluna.cor }}
    >
      <header className="coluna-topo">
        <span className="ponto" />
        <h2>{coluna.nome}</h2>
        <span className="contador">{quantidade}</span>
        {podeCriar && (
          <button
            type="button"
            className="botao-adicionar"
            aria-label={criando ? 'Fechar novo cartão' : 'Novo cartão'}
            aria-expanded={criando}
            onClick={onAlternarCriacao}
          >
            <Plus size={14} />
          </button>
        )}
      </header>

      {festa > 0 && <Faiscas key={festa} />}

      <div className="coluna-lista">
        {formulario}
        {children}
        {alvo && <div className="solte-aqui">Solte para mover para {coluna.nome}</div>}
        {quantidade === 0 && !formulario && !alvo && (
          <p className="coluna-vazia">
            {vemDeOutraColuna
              ? 'Solte um cartão aqui'
              : podeCriar
                ? 'Nenhum cartão. Use o + para criar.'
                : 'Nenhum cartão aqui'}
          </p>
        )}
      </div>
    </section>
  )
}
