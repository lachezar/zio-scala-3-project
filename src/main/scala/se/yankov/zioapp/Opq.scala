package se.yankov.zioapp

trait Opq[In, Out]:
  def pack(value: In): Out
  extension (wrapper: Out) def unpack: In
