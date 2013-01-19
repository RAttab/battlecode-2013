#!/usr/bin/env python
# -*- coding: utf-8 -*- 

# **********
# Filename: 		mapedit.py
# Description: 		A battlecode map editor
# Author:			Marc Vieira Cardinal
# Creation Date: 	January 16, 2013
# Revision Date: 	January 19, 2013
# **********

# General imports
import time
from xml.dom.minidom import parseString

# UI import

try:
    import Tkinter
    import tkFileDialog
    import tkSimpleDialog
except ImportError:
    raise ImportError, "Please install python-tk to run this program."

class MapCanvas():
    # Land, Mine, Encampment, Team A, Team B
    states = ['.', 'o', '+', 'A', 'B']
    statesCol = [ '#000000', '#dd0000', '#0000dd', '#aa00aa', '#aa00aa' ]

    def __init__(self, canvas, width = 20, height = 20):
        self.canvas = canvas
        self.gridWidth = width
        self.gridHeight = height

        # Used to display mouse pos overlay
        self.mouseIn = False
        self.mouseX = 0
        self.mouseY = 0

        # Initialized for the first time in Draw(), updated in OnEvent()
        self.cWidth = None
        self.cHeight = None

        self.canvas.config(bg = '#999999')

        self.cells = self.GenEmptyGrid(width, height)

        # Set a trigger for resize events
        self.canvas.bind("<Configure>", self.OnEvent)
        self.canvas.bind("<Button-1>", self.OnClickLeft)
        self.canvas.bind("<Button-2>", self.OnClickRight)
        self.canvas.bind("<Button-3>", self.OnClickRight)
        self.canvas.bind("<Enter>", self.OnMouseEnter)
        self.canvas.bind("<Motion>", self.OnMouseMove)
        self.canvas.bind("<Leave>", self.OnMouseLeave)

        # Initial draw
        self.Draw()

    def GenEmptyGrid(self, width, height):
        # cells will be a two dimentional array of cells[x][y]
        cells = []
        for col in range(0, width):
            cells.append([[]] * height)

            for row in range(0, height):
                cells[col][row] = 0

        return cells

    def Draw(self):
        if not self.cWidth or not self.cHeight:
            self.cWidth = int(self.canvas.cget("width"))
            self.cHeight = int(self.canvas.cget("height"))

        # Clear the canvas
        self.canvas.delete("all")

        # Draw mouse overlay
        if self.mouseIn:
            self.canvas.create_rectangle(self.mouseX * self.cWidth / self.gridWidth,
                                         0,
                                         (self.mouseX+1) * self.cWidth / self.gridWidth,
                                         self.cHeight,
                                         fill = "#d0d0ff")
            self.canvas.create_rectangle(0,
                                         self.mouseY * self.cHeight / self.gridHeight,
                                         self.cWidth,
                                         (self.mouseY+1) * self.cHeight / self.gridHeight,
                                         fill = "#d0d0ff")

        # Draw grid lines
        for col in range(0, self.gridWidth):
            x = col * self.cWidth / self.gridWidth
            self.canvas.create_line(x, 0, x, self.cHeight, fill = "#888888")
        for row in range(0, self.gridHeight):   
            y = row * self.cHeight / self.gridHeight
            self.canvas.create_line(0, y, self.cWidth, y, fill = "#888888")

        if self.mouseIn:
            self.canvas.create_text((self.mouseX+1) * self.cWidth / self.gridWidth,
                        (self.mouseY-1) * self.cHeight / self.gridHeight,
                        text = "(%s,%s)" % (self.mouseX, self.mouseY))

        # Calculate object offset
        offX = self.cWidth / self.gridWidth / 2
        offY = self.cHeight / self.gridHeight / 2

        # Draw the objects
        for col in range(0, self.gridWidth):
            for row in range(0, self.gridHeight):
                x = col * self.cWidth / self.gridWidth
                y = row * self.cHeight / self.gridHeight

                self.canvas.create_text(x + offX, y + offY,
                                        text = self.states[self.cells[col][row]],
                                        fill = self.statesCol[self.cells[col][row]])

        self.canvas.update()

    def New(self):
        self.mapDef = self.GetDefaultMapDefinition()
        self.mapDef['width'] = self.gridWidth
        self.mapDef['height'] = self.gridHeight
        self.filename = ""
        self.LoadFromMapDef()

    def GetDefaultMapDefinition(self):
        return { 'width': 1,
                 'height': 1,
                 'seed': 55555,
                 'rounds': 2000,
                 'symbols': [''] * 5,
                 'data': ""}

    def LoadFromFile(self, filename):
        file = open(filename,'r')
        data = file.read()
        file.close()

        dom = parseString(data)

        mapDef = self.GetDefaultMapDefinition()

        for (key, value) in dom.getElementsByTagName('map')[0].attributes.items():
            key = key.lower()
            if key in mapDef:
                mapDef[key] = int(value)

        for (key, value) in dom.getElementsByTagName('game')[0].attributes.items():
            key = key.lower()
            if key in mapDef:
                mapDef[key] = int(value)

        for symbol in dom.getElementsByTagName('symbol'):
            properties = {}
            for (key, value) in symbol.attributes.items():
                properties[key.lower()] = value

            properties['type'] = properties['type'].upper()

            if properties['type'] == "TERRAIN":
                mapDef['symbols'][0] = properties['character']
            elif properties['type'] == "MINE":
                mapDef['symbols'][1] = properties['character']
            elif properties['type'] == "ENCAMPMENT":
                mapDef['symbols'][2] = properties['character']
            elif properties['type'] == "HQ":
                mapDef['symbols'][3 if mapDef['symbols'][3] == '' else 4] = properties['character']

        mapDef['data'] = dom.getElementsByTagName('data')[0].firstChild.wholeText.strip()

        self.mapDef = mapDef
        self.LoadFromMapDef()

    def SaveToFile(self, filename):
        self.SaveToMapDef()

        file = open(filename,'w')
        file.write('\n'.join(['<?xml version="1.0" encoding="UTF-8"?>',
                              '<map height="%s" width="%s">' % (self.mapDef['height'], self.mapDef['width']),
                              '    <game seed="%s" rounds="%s"/>' % (self.mapDef['seed'], self.mapDef['rounds']),
                              '    <symbols>',
                              '        <symbol terrain="LAND" type="TERRAIN" character="%s"/>' % self.mapDef['symbols'][0],
                              '        <symbol team="NEUTRAL" type="MINE" character="%s"/>' % self.mapDef['symbols'][1],
                              '        <symbol team="NEUTRAL" type="ENCAMPMENT" character="%s"/>' % self.mapDef['symbols'][2],
                              '        <symbol team="A" type="HQ" character="%s"/>' % self.mapDef['symbols'][3],
                              '        <symbol team="B" type="HQ" character="%s"/>' % self.mapDef['symbols'][4],
                              '    </symbols>',
                              '    <data>',
                              '<![CDATA[',
                              '%s' % self.mapDef['data'],
                              ']]>',
                              '    </data>',
                              '</map>']))
        file.close()

    def LoadFromMapDef(self):
        self.gridWidth = self.mapDef['width']
        self.gridHeight = self.mapDef['height']
        self.cells = self.GenEmptyGrid(self.gridWidth, self.gridHeight)

        col, row = 0, 0
        for char in self.mapDef['data'].replace("\n", ""):
            self.cells[col][row] = self.mapDef['symbols'].index(char)
            col += 1

            if col >= self.gridWidth:
                col = 0
                row += 1

        self.Draw()

    def SaveToMapDef(self):
        self.mapDef = self.GetDefaultMapDefinition()
        self.mapDef['width'] = self.gridWidth
        self.mapDef['height'] = self.gridHeight
        self.mapDef['symbols'] = self.states

        self.mapDef['data'] = ""
        for row in range(0, slf.gridHeight):
            for col in range(0, self.gridWidth):
                self.mapDef['data'] += self.states[self.cells[col][row]]
            self.mapDef['data'] += "\n"
        self.mapDef['data'] = self.mapDef['data'].strip("\n") # Remove the last \n

    def ResizeGrid(self, width, height, clear = False):
        self.gridWidth = width
        self.gridHeight = height
        newCells = self.GenEmptyGrid(width, height)
        for col in range(0, len(newCells)):
            for row in range(0, len(newCells[col])):
                if col < len(self.cells) and row < len(self.cells[col]):
                    newCells[col][row] = self.cells[col][row]
                else:
                    newCells[col][row] = 0
        self.cells = newCells
        self.Draw()

    def CoordsToGridXY(self, mouseX, mouseY):
        return int(float(mouseX) / float(self.cWidth) * self.gridWidth), int(float(mouseY) / float(self.cHeight) * self.gridHeight)

    def OnEvent(self, event):
        eventType = int(event.type)

        # Resize event
        if eventType == 22:
            self.cWidth = event.width
            self.cHeight = event.height
            self.Draw()

    def OnClickLeft(self, event):
        x, y = self.CoordsToGridXY(event.x, event.y)
        cellVal = self.cells[x][y]
        self.cells[x][y] = (cellVal + 1 if cellVal < len(self.states)-1 else 0)
        self.Draw()

    def OnClickRight(self, event):
        x, y = self.CoordsToGridXY(event.x, event.y)
        self.cells[x][y] = 0
        self.Draw()

    def OnMouseEnter(self, event):
        self.mouseIn = True
        self.Draw()

    def OnMouseMove(self, event):
        self.mouseX, self.mouseY = self.CoordsToGridXY(event.x, event.y)
        self.Draw()

    def OnMouseLeave(self, event):
        self.mouseIn = False
        self.Draw()


class MapEdit(Tkinter.Tk):
    def __init__(self, parent):
        Tkinter.Tk.__init__(self, parent)
        self.parent = parent

        self.InitializeUI()

        self.filename = ""
        self.canvas = MapCanvas(self.cvMap)

    def InitializeUI(self):
        self.grid()
        self.grid_columnconfigure(0, weight = 1)
        self.grid_rowconfigure(1, weight = 1)

        self.frameButtons = Tkinter.Frame(self)
        self.frameButtons.grid(column = 0, row = 0)

        btnNew = Tkinter.Button(self.frameButtons,
                                text = "New",
                                command = self.OnNewClick)
        btnNew.grid(column = 0, row = 0)

        btnOpen = Tkinter.Button(self.frameButtons,
                                 text = "Open",
                                 command = self.OnOpenClick)
        btnOpen.grid(column = 1, row = 0)

        btnSaveAs = Tkinter.Button(self.frameButtons,
                                   text = "Save As",
                                   command = self.OnSaveAsClick)
        btnSaveAs.grid(column = 2, row = 0)

        btnSize = Tkinter.Button(self.frameButtons,
                                 text = "Size",
                                 command = self.OnSizeClick)
        btnSize.grid(column = 3, row = 0)

        self.frameMap = Tkinter.Frame(self)
        self.frameMap.grid(column = 0, row = 1, sticky = "nswe")

        self.cvMap = Tkinter.Canvas(self.frameMap, width = 250, height = 250)
        self.cvMap.pack(fill = "both", expand = 1)

    def LoadFile(self, filename):
        self.filename = filename
        self.canvas.LoadFromFile(filename)

    def SaveFile(self, filename):
        self.filename = filename
        self.canvas.SaveToFile(filename)

    ###
    # Click handlers
    ###
    def OnNewClick(self):
        self.canvas.New()

    def OnOpenClick(self):
        filename = tkFileDialog.askopenfilename(filetypes=[("Battlecode map files", "*.xml")])
        if filename:
            self.LoadFile(filename)

    def OnSaveAsClick(self):
        filename = tkFileDialog.asksaveasfilename(filetypes=[("Battlecode map files", "*.xml")])
        if filename:
            self.SaveFile(filename)

    def OnSizeClick(self):
        width = tkSimpleDialog.askinteger("Size",
                                          "Width:",
                                          initialvalue = self.canvas.gridWidth,
                                          parent = self,
                                          minvalue = 2,
                                          maxvalue = 100)
        height = tkSimpleDialog.askinteger("Size",
                                           "Height:",
                                           initialvalue = self.canvas.gridHeight,
                                           parent = self,
                                           minvalue = 2,
                                           maxvalue = 100)
        if not width:
            width = self.canvas.gridWidth
        if not height:
            height = self.canvas.gridHeight

        self.canvas.ResizeGrid(width, height)

if __name__ == "__main__":
        app = MapEdit(None)
        app.title("BattleCode - 2013 - Map Editor")
        app.mainloop()