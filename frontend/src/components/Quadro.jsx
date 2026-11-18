import { useState } from 'react'
import { DndContext, DragOverlay, PointerSensor, TouchSensor, useSensor, useSensors } from '@dnd-kit/core'
import { COLUNAS } from '../colunas'
import Cartao, { CartaoFlutuante } from './Cartao'
import Coluna from './Coluna'
import NovoCartao from './NovoCartao'

const nomeDaColuna = (id) => COLUNAS.find((c) => c.id === id)?.nome ?? id

export default function Quadro({ cartoes, onAbrir, onCriar, onMover, onRemover, onAdicionarExtra }) {
  const [criando, setCriando] = useState(false)
  const [arrastado, setArrastado] = useState(null)

  const sensores = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 6 } }),
    useSensor(TouchSensor, { activationConstraint: { delay: 250, tolerance: 8 } }),
  )

  const titulo = (id) => cartoes.find((c) => c.id === id)?.titulo ?? 'cartão'
  const anuncios = {
    onDragStart: ({ active }) => `Cartão ${titulo(active.id)} pego.`,
    onDragOver: ({ active, over }) =>
      over ? `Cartão ${titulo(active.id)} sobre a coluna ${nomeDaColuna(over.id)}.` : `Cartão ${titulo(active.id)} fora das colunas.`,
    onDragEnd: ({ active, over }) =>
      over ? `Cartão ${titulo(active.id)} solto em ${nomeDaColuna(over.id)}.` : `Cartão ${titulo(active.id)} voltou ao lugar.`,
    onDragCancel: ({ active }) => `Movimento do cartão ${titulo(active.id)} cancelado.`,
  }

  function soltar({ active, over }) {
    setArrastado(null)
    const cartao = cartoes.find((c) => c.id === active.id)
    if (cartao && over && over.id !== cartao.coluna) onMover(cartao.id, over.id)
  }

  return (
    <DndContext
      sensors={sensores}
      accessibility={{ announcements: anuncios }}
      onDragStart={({ active }) => setArrastado(cartoes.find((c) => c.id === active.id) ?? null)}
      onDragEnd={soltar}
      onDragCancel={() => setArrastado(null)}
    >
      <div className="quadro">
        {COLUNAS.map((coluna) => {
          const daColuna = cartoes.filter((c) => c.coluna === coluna.id)
          const comFormulario = coluna.id === 'A_FAZER' && criando
          return (
            <Coluna
              key={coluna.id}
              coluna={coluna}
              quantidade={daColuna.length}
              arrastado={arrastado}
              criando={criando}
              onAlternarCriacao={() => setCriando((v) => !v)}
              formulario={comFormulario && <NovoCartao onCriar={onCriar} onFechar={() => setCriando(false)} />}
            >
              {daColuna.map((c) => (
                <Cartao
                  key={c.id}
                  cartao={c}
                  onAbrir={onAbrir}
                  onMover={onMover}
                  onRemover={onRemover}
                  onAdicionarExtra={onAdicionarExtra}
                />
              ))}
            </Coluna>
          )
        })}
      </div>
      <DragOverlay>{arrastado && <CartaoFlutuante cartao={arrastado} />}</DragOverlay>
    </DndContext>
  )
}
